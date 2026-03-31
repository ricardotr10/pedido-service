package com.rest.api.infrastructure.adapter.out.repository.impl;

import com.rest.api.domain.model.Pedido;
import com.rest.api.domain.port.out.PedidoRepositoryPort;
import com.rest.api.infrastructure.adapter.out.entity.PedidoEntity;
import com.rest.api.infrastructure.adapter.out.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {
    
    private final PedidoRepository pedidoRepository;
    
    @Override
    public Pedido save(Pedido pedido) {
        PedidoEntity entity = toEntity(pedido);
        PedidoEntity saved = pedidoRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public List<Pedido> saveAll(List<Pedido> pedidos) {
        List<PedidoEntity> entities = pedidos.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
        List<PedidoEntity> saved = pedidoRepository.saveAll(entities);
        return saved.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Pedido> findByNumeroPedido(String numeroPedido) {
        return pedidoRepository.findByNumeroPedido(numeroPedido)
            .map(this::toDomain);
    }
    
    @Override
    public Set<String> findAllByNumeroPedidoIn(Set<String> numeros) {
        return pedidoRepository.findByNumeroPedidoIn(numeros)
                .stream()
                .map(PedidoEntity::getNumeroPedido)
                .collect(Collectors.toSet());
    }
    
    @Override
    public boolean existsByNumeroPedido(String numeroPedido) {
        return pedidoRepository.existsByNumeroPedido(numeroPedido);
    }
    
    private PedidoEntity toEntity(Pedido domain) {
        return PedidoEntity.builder()
        	.id(null)
            .numeroPedido(domain.getNumeroPedido())
            .clienteId(domain.getClienteId())
            .zonaId(domain.getZonaId())
            .fechaEntrega(domain.getFechaEntrega())
            .estado(domain.getEstado() != null 
            ? PedidoEntity.EstadoPedido.valueOf(domain.getEstado().name())
            : null)
            .requiereRefrigeracion(domain.getRequiereRefrigeracion())
            .build();
    }
    
    private Pedido toDomain(PedidoEntity entity) {
        return Pedido.builder()
            .id(entity.getId())
            .numeroPedido(entity.getNumeroPedido())
            .clienteId(entity.getClienteId())
            .zonaId(entity.getZonaId())
            .fechaEntrega(entity.getFechaEntrega())
            .estado(Pedido.EstadoPedido.valueOf(entity.getEstado().name()))
            .requiereRefrigeracion(entity.getRequiereRefrigeracion())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}