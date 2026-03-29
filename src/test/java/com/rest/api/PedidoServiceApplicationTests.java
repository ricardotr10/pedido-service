package com.rest.api;

import com.rest.api.domain.model.Cliente;
import com.rest.api.domain.model.Pedido;
import com.rest.api.domain.service.PedidoValidator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PedidoValidatorTest {

    private final PedidoValidator validator = new PedidoValidator();

    @Test
    void debe_fallar_fecha_pasada() {

        Pedido p = Pedido.builder()
                .numeroPedido("P001")
                .fechaEntrega(LocalDate.now().minusDays(1))
                .estado(Pedido.EstadoPedido.PENDIENTE)
                .clienteId("C1")
                .zonaId("Z1")
                .requiereRefrigeracion(false)
                .build();

        Optional<PedidoValidator.ErrorType> result =
                validator.validar(p, Map.of(), Map.of());

        assertTrue(result.isPresent());
    }
    
    @Test
    void debe_fallar_cliente_inactivo() {
        Pedido p = Pedido.builder()
                .numeroPedido("P001")
                .fechaEntrega(LocalDate.now().plusDays(1))
                .estado(Pedido.EstadoPedido.PENDIENTE)
                .clienteId("C1")
                .zonaId("Z1")
                .requiereRefrigeracion(false)
                .build();

        Map<String, Cliente> clientes = Map.of(
                "C1", Cliente.builder().id("C1").activo(false).build()
        );

        Optional<PedidoValidator.ErrorType> result =
                validator.validar(p, clientes, Map.of());

        assertEquals(PedidoValidator.ErrorType.CLIENTE_NO_ENCONTRADO, result.get());
    }
}