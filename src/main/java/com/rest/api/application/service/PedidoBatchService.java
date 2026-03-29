package com.rest.api.application.service;

import com.rest.api.application.dto.CargaPedidosResponse;
import com.rest.api.application.dto.ErrorDetalle;
import com.rest.api.domain.model.Pedido;
import com.rest.api.domain.port.out.PedidoRepositoryPort;
import com.rest.api.domain.service.PedidoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoBatchService {
    
    private final PedidoValidator validator;
    private final PedidoRepositoryPort pedidoRepository;
    
    @Value("${app.batch.size:500}")
    private int batchSize;
    
    @Transactional
    public CargaPedidosResponse procesarPedidos(List<Pedido> pedidos) {
        log.info("Iniciando procesamiento batch de {} pedidos", pedidos.size());
        
        List<Pedido> pedidosValidos = new ArrayList<>();
        List<ErrorDetalle> errores = new ArrayList<>();
        Map<String, List<Integer>> erroresPorTipo = new HashMap<>();
        
        int numeroLinea = 2;
        
        for (Pedido pedido : pedidos) {
            Optional<PedidoValidator.ErrorType> errorOpt = validator.validar(pedido);
            
            if (errorOpt.isEmpty()) {
                pedidosValidos.add(pedido);
            } else {
                String errorType = errorOpt.get().name();
                errores.add(ErrorDetalle.builder()
                    .linea(numeroLinea)
                    .motivo(errorType)
                    .build());
                erroresPorTipo.computeIfAbsent(errorType, k -> new ArrayList<>()).add(numeroLinea);
            }
            numeroLinea++;
        }
        
        int guardados = 0;
        if (!pedidosValidos.isEmpty()) {
            for (int i = 0; i < pedidosValidos.size(); i += batchSize) {
                int end = Math.min(i + batchSize, pedidosValidos.size());
                List<Pedido> batch = pedidosValidos.subList(i, end);
                pedidoRepository.saveAll(batch);
                guardados += batch.size();
            }
        }
        
        Map<String, Long> erroresAgrupados = erroresPorTipo.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));
        
        return CargaPedidosResponse.builder()
            .totalProcesados(pedidos.size())
            .guardados(guardados)
            .conError(errores.size())
            .errores(errores)
            .erroresAgrupados(erroresAgrupados)
            .build();
    }
}