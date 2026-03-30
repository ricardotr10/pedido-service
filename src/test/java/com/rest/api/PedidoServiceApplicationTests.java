package com.rest.api;

import com.rest.api.domain.model.Cliente;
import com.rest.api.domain.model.Pedido;
import com.rest.api.domain.model.Zona;
import com.rest.api.domain.service.PedidoValidator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PedidoValidatorTest {

    private final PedidoValidator validator = new PedidoValidator();

    private Pedido buildBasePedido() {
        return Pedido.builder()
                .numeroPedido("P001")
                .fechaEntrega(LocalDate.now().plusDays(1))
                .estado(Pedido.EstadoPedido.PENDIENTE)
                .clienteId("C1")
                .zonaId("Z1")
                .requiereRefrigeracion(false)
                .build();
    }

    @Test
    void debe_fallar_fecha_pasada() {
        Pedido p = buildBasePedido().toBuilder()
                .fechaEntrega(LocalDate.now().minusDays(1))
                .build();

        Optional<PedidoValidator.ErrorType> result =
                validator.validar(p, Map.of(), Map.of());

        assertEquals(PedidoValidator.ErrorType.FECHA_INVALIDA, result.get());
    }

    @Test
    void debe_fallar_formato_numeroPedido() {
        Pedido p = buildBasePedido().toBuilder()
                .numeroPedido("P-001!") // inválido
                .build();

        Optional<PedidoValidator.ErrorType> result =
                validator.validar(p, Map.of(), Map.of());

        assertEquals(PedidoValidator.ErrorType.FORMATO_INVALIDO, result.get());
    }

    @Test
    void debe_fallar_estado_null() {
        Pedido p = buildBasePedido().toBuilder()
                .estado(null)
                .build();

        Optional<PedidoValidator.ErrorType> result =
                validator.validar(p, Map.of(), Map.of());

        assertEquals(PedidoValidator.ErrorType.ESTADO_INVALIDO, result.get());
    }

    @Test
    void debe_fallar_cliente_inactivo() {
        Pedido p = buildBasePedido();

        Map<String, Cliente> clientes = Map.of(
                "C1", Cliente.builder().id("C1").activo(false).build()
        );

        Optional<PedidoValidator.ErrorType> result =
                validator.validar(p, clientes, Map.of());

        assertEquals(PedidoValidator.ErrorType.CLIENTE_NO_ENCONTRADO, result.get());
    }

    @Test
    void debe_fallar_cliente_no_existe() {
        Pedido p = buildBasePedido();

        Optional<PedidoValidator.ErrorType> result =
                validator.validar(p, Map.of(), Map.of());

        assertEquals(PedidoValidator.ErrorType.CLIENTE_NO_ENCONTRADO, result.get());
    }

    @Test
    void debe_fallar_zona_no_existe() {
        Pedido p = buildBasePedido();

        Map<String, Cliente> clientes = Map.of(
                "C1", Cliente.builder().id("C1").activo(true).build()
        );

        Optional<PedidoValidator.ErrorType> result =
                validator.validar(p, clientes, Map.of());

        assertEquals(PedidoValidator.ErrorType.ZONA_INVALIDA, result.get());
    }

    @Test
    void debe_fallar_cadena_frio_no_soportada() {
        Pedido p = buildBasePedido().toBuilder()
                .requiereRefrigeracion(true)
                .build();

        Map<String, Cliente> clientes = Map.of(
                "C1", Cliente.builder().id("C1").activo(true).build()
        );

        Map<String, Zona> zonas = Map.of(
                "Z1", Zona.builder().id("Z1").soporteRefrigeracion(false).build()
        );

        Optional<PedidoValidator.ErrorType> result =
                validator.validar(p, clientes, zonas);

        assertEquals(PedidoValidator.ErrorType.CADENA_FRIO_NO_SOPORTADA, result.get());
    }

    @Test
    void debe_ser_valido() {
        Pedido p = buildBasePedido();

        Map<String, Cliente> clientes = Map.of(
                "C1", Cliente.builder().id("C1").activo(true).build()
        );

        Map<String, Zona> zonas = Map.of(
                "Z1", Zona.builder().id("Z1").soporteRefrigeracion(true).build()
        );

        Optional<PedidoValidator.ErrorType> result =
                validator.validar(p, clientes, zonas);

        assertTrue(result.isEmpty());
    }
}