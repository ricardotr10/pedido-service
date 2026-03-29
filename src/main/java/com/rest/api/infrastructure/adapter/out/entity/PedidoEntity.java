package com.rest.api.infrastructure.adapter.out.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "numero_pedido", unique = true, nullable = false)
    private String numeroPedido;
    
    @Column(name = "cliente_id", nullable = false)
    private String clienteId;
    
    @Column(name = "zona_id", nullable = false)
    private String zonaId;
    
    @Column(name = "fecha_entrega", nullable = false)
    private LocalDate fechaEntrega;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;
    
    @Column(name = "requiere_refrigeracion", nullable = false)
    private Boolean requiereRefrigeracion;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum EstadoPedido {
        PENDIENTE, CONFIRMADO, ENTREGADO
    }
}