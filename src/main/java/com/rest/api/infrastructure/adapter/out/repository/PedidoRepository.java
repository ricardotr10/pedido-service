package com.rest.api.infrastructure.adapter.out.repository;

import com.rest.api.infrastructure.adapter.out.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, UUID> {
    Optional<PedidoEntity> findByNumeroPedido(String numeroPedido);
    boolean existsByNumeroPedido(String numeroPedido);
}