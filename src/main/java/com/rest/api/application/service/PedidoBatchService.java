package com.rest.api.application.service;

import com.rest.api.application.dto.*;
import com.rest.api.domain.model.*;
import com.rest.api.domain.port.out.*;
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
    private final ClienteRepositoryPort clienteRepository;
    private final ZonaRepositoryPort zonaRepository;

    @Value("${app.batch.size:500}")
    private int batchSize;

    @Transactional
    public CargaPedidosResponse procesarPedidos(List<Pedido> pedidos) {

        List<Pedido> validos = new ArrayList<>();
        List<ErrorDetalle> errores = new ArrayList<>();
        Map<String, List<Integer>> erroresPorTipo = new HashMap<>();

        Set<String> numerosVistos = new HashSet<>();

        Map<String, Cliente> clientes = clienteRepository.findAllByIds(
                pedidos.stream().map(Pedido::getClienteId).distinct().toList()
        ).stream().collect(Collectors.toMap(Cliente::getId, c -> c));

        Map<String, Zona> zonas = zonaRepository.findAllByIds(
                pedidos.stream().map(Pedido::getZonaId).distinct().toList()
        ).stream().collect(Collectors.toMap(Zona::getId, z -> z));

        Set<String> existentesEnBd = pedidoRepository.findAllByNumeroPedidoIn(
                pedidos.stream().map(Pedido::getNumeroPedido).collect(Collectors.toSet())
        );

        int linea = 2;

        for (Pedido p : pedidos) {

            if (!numerosVistos.add(p.getNumeroPedido()) ||
                existentesEnBd.contains(p.getNumeroPedido())) {

                addError("DUPLICADO", linea, errores, erroresPorTipo);
                linea++;
                continue;
            }

            Optional<PedidoValidator.ErrorType> error =
                    validator.validar(p, clientes, zonas);

            if (error.isPresent()) {
                addError(error.get().name(), linea, errores, erroresPorTipo);
            } else {
                validos.add(p);
            }

            linea++;
        }

        int guardados = 0;

        for (int i = 0; i < validos.size(); i += batchSize) {
            List<Pedido> batch =
                    validos.subList(i, Math.min(i + batchSize, validos.size()));

            pedidoRepository.saveAll(batch);
            guardados += batch.size();
        }

        Map<String, Long> agrupados = erroresPorTipo.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (long) e.getValue().size()
                ));

        return CargaPedidosResponse.builder()
                .totalProcesados(pedidos.size())
                .guardados(guardados)
                .conError(errores.size())
                .errores(errores)
                .erroresAgrupados(agrupados)
                .build();
    }

    private void addError(String tipo, int linea,
                          List<ErrorDetalle> errores,
                          Map<String, List<Integer>> map) {

        errores.add(ErrorDetalle.builder()
                .linea(linea)
                .motivo(tipo)
                .build());

        map.computeIfAbsent(tipo, k -> new ArrayList<>()).add(linea);
    }
}