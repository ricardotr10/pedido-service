package com.rest.api.domain.service;

import com.rest.api.domain.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

@Component
public class PedidoValidator {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    public enum ErrorType {
        CLIENTE_NO_ENCONTRADO,
        ZONA_INVALIDA,
        FECHA_INVALIDA,
        ESTADO_INVALIDO,
        DUPLICADO,
        CADENA_FRIO_NO_SOPORTADA,
        FORMATO_INVALIDO
    }

    public Optional<ErrorType> validar(
            Pedido p,
            Map<String, Cliente> clientes,
            Map<String, Zona> zonas) {

        if (p.getNumeroPedido() == null ||
            !p.getNumeroPedido().matches("^[a-zA-Z0-9]+$")) {
            return Optional.of(ErrorType.FORMATO_INVALIDO);
        }

        if (p.getEstado() == null) {
            return Optional.of(ErrorType.ESTADO_INVALIDO);
        }

        if (p.getFechaEntrega() == null ||
            p.getFechaEntrega().isBefore(LocalDate.now(LIMA))) {
            return Optional.of(ErrorType.FECHA_INVALIDA);
        }

        Cliente cliente = clientes.get(p.getClienteId());
        if (cliente == null || !cliente.getActivo()) {
            return Optional.of(ErrorType.CLIENTE_NO_ENCONTRADO);
        }

        Zona zona = zonas.get(p.getZonaId());
        if (zona == null) {
            return Optional.of(ErrorType.ZONA_INVALIDA);
        }

        if (Boolean.TRUE.equals(p.getRequiereRefrigeracion()) &&
            !zona.getSoporteRefrigeracion()) {
            return Optional.of(ErrorType.CADENA_FRIO_NO_SOPORTADA);
        }

        return Optional.empty();
    }
}