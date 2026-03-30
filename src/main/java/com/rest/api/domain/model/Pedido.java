package com.rest.api.domain.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class Pedido {
    private UUID id;
    private String numeroPedido;
    private String clienteId;
    private String zonaId;
    private LocalDate fechaEntrega;
    private EstadoPedido estado;
    private Boolean requiereRefrigeracion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum EstadoPedido {
        PENDIENTE, CONFIRMADO, ENTREGADO
    }
}