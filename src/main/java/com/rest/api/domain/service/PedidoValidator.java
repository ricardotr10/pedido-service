package com.rest.api.domain.service;

import com.rest.api.domain.model.Pedido;
import com.rest.api.domain.model.Zona;
import com.rest.api.domain.port.out.ClienteRepositoryPort;
import com.rest.api.domain.port.out.PedidoRepositoryPort;
import com.rest.api.domain.port.out.ZonaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoValidator {
    
    private final ClienteRepositoryPort clienteRepository;
    private final ZonaRepositoryPort zonaRepository;
    private final PedidoRepositoryPort pedidoRepository;
    
    private static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");
    
    public enum ErrorType {
        CLIENTE_NO_ENCONTRADO,
        ZONA_INVALIDA,
        FECHA_INVALIDA,
        ESTADO_INVALIDO,
        DUPLICADO,
        CADENA_FRIO_NO_SOPORTADA
    }
    
    public Optional<ErrorType> validar(Pedido pedido) {
        if (pedido.getEstado() == null) {
            return Optional.of(ErrorType.ESTADO_INVALIDO);
        }
        
        LocalDate today = LocalDate.now(LIMA_ZONE);
        if (pedido.getFechaEntrega().isBefore(today)) {
            return Optional.of(ErrorType.FECHA_INVALIDA);
        }
        
        if (!clienteRepository.existsById(pedido.getClienteId())) {
            return Optional.of(ErrorType.CLIENTE_NO_ENCONTRADO);
        }
        
        Optional<Zona> zonaOpt = zonaRepository.findById(pedido.getZonaId());
        if (zonaOpt.isEmpty()) {
            return Optional.of(ErrorType.ZONA_INVALIDA);
        }
        
        Zona zona = zonaOpt.get();
        
        if (pedido.getRequiereRefrigeracion() != null && 
            pedido.getRequiereRefrigeracion() && 
            !zona.getSoporteRefrigeracion()) {
            return Optional.of(ErrorType.CADENA_FRIO_NO_SOPORTADA);
        }
        
        if (pedidoRepository.existsByNumeroPedido(pedido.getNumeroPedido())) {
            return Optional.of(ErrorType.DUPLICADO);
        }
        
        return Optional.empty();
    }
}