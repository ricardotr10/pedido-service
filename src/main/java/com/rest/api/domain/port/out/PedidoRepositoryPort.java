package com.rest.api.domain.port.out;

import com.rest.api.domain.model.Pedido;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PedidoRepositoryPort {

    Pedido save(Pedido pedido);

    List<Pedido> saveAll(List<Pedido> pedidos);

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    boolean existsByNumeroPedido(String numeroPedido);

    // 🔥 batch duplicados DB
    Set<String> findAllByNumeroPedidoIn(Set<String> numeros);
}