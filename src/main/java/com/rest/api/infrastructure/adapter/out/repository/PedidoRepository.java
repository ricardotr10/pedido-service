package com.rest.api.infrastructure.adapter.out.repository;

import com.rest.api.infrastructure.adapter.out.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<PedidoEntity, UUID> {

    Optional<PedidoEntity> findByNumeroPedido(String numeroPedido);

    boolean existsByNumeroPedido(String numeroPedido);

    List<PedidoEntity> findByNumeroPedidoIn(Set<String> numeros);
}