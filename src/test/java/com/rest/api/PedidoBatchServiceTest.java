package com.rest.api;

import com.rest.api.application.dto.CargaPedidosResponse;
import com.rest.api.application.service.PedidoBatchService;
import com.rest.api.domain.model.Cliente;
import com.rest.api.domain.model.Pedido;
import com.rest.api.domain.model.Zona;
import com.rest.api.domain.port.out.ClienteRepositoryPort;
import com.rest.api.domain.port.out.PedidoRepositoryPort;
import com.rest.api.domain.port.out.ZonaRepositoryPort;
import com.rest.api.domain.service.PedidoValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoBatchServiceTest {

    @Mock
    private PedidoValidator validator;

    @Mock
    private PedidoRepositoryPort pedidoRepository;

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private ZonaRepositoryPort zonaRepository;

    @InjectMocks
    private PedidoBatchService service;

    @BeforeEach
    void setup() throws Exception {
        Field field = PedidoBatchService.class.getDeclaredField("batchSize");
        field.setAccessible(true);
        field.set(service, 500);
    }

    private Pedido buildPedido(String numero) {
        return Pedido.builder()
                .numeroPedido(numero)
                .clienteId("C1")
                .zonaId("Z1")
                .fechaEntrega(LocalDate.now().plusDays(1))
                .estado(Pedido.EstadoPedido.PENDIENTE)
                .requiereRefrigeracion(false)
                .build();
    }

    private void mockCatalogosValidos() {
        when(clienteRepository.findAllByIds(any()))
                .thenReturn(List.of(
                        Cliente.builder().id("C1").activo(true).build()
                ));

        when(zonaRepository.findAllByIds(any()))
                .thenReturn(List.of(
                        Zona.builder().id("Z1").soporteRefrigeracion(true).build()
                ));
    }

    @Test
    void debe_guardar_pedidos_validos() {

        Pedido p1 = buildPedido("P1");
        Pedido p2 = buildPedido("P2");

        mockCatalogosValidos();

        when(pedidoRepository.findAllByNumeroPedidoIn(any()))
                .thenReturn(Set.of());

        when(validator.validar(any(), any(), any()))
                .thenReturn(Optional.empty());

        CargaPedidosResponse response =
                service.procesarPedidos(List.of(p1, p2));

        assertEquals(2, response.getTotalProcesados());
        assertEquals(2, response.getGuardados());
        assertEquals(0, response.getConError());

        verify(pedidoRepository, times(1)).saveAll(any());
    }

    @Test
    void no_debe_guardar_duplicados_en_bd() {

        Pedido p1 = buildPedido("P1");

        mockCatalogosValidos();

        when(pedidoRepository.findAllByNumeroPedidoIn(any()))
                .thenReturn(Set.of("P1"));

        CargaPedidosResponse response =
                service.procesarPedidos(List.of(p1));

        assertEquals(1, response.getTotalProcesados());
        assertEquals(0, response.getGuardados());
        assertEquals(1, response.getConError());

        verify(pedidoRepository, never()).saveAll(any());
    }

    @Test
    void debe_marcar_error_validacion() {

        Pedido p1 = buildPedido("P1");

        mockCatalogosValidos();

        when(pedidoRepository.findAllByNumeroPedidoIn(any()))
                .thenReturn(Set.of());

        when(validator.validar(any(), any(), any()))
                .thenReturn(Optional.of(PedidoValidator.ErrorType.CLIENTE_NO_ENCONTRADO));

        CargaPedidosResponse response =
                service.procesarPedidos(List.of(p1));

        assertEquals(1, response.getTotalProcesados());
        assertEquals(0, response.getGuardados());
        assertEquals(1, response.getConError());

        assertTrue(response.getErroresAgrupados()
                .containsKey("CLIENTE_NO_ENCONTRADO"));
    }

    @Test
    void no_debe_guardar_duplicados_en_mismo_archivo() {

        Pedido p1 = buildPedido("P1");
        Pedido p2 = buildPedido("P1"); // duplicado en memoria

        mockCatalogosValidos();

        when(pedidoRepository.findAllByNumeroPedidoIn(any()))
                .thenReturn(Set.of());

        when(validator.validar(any(), any(), any()))
                .thenReturn(Optional.empty());

        CargaPedidosResponse response =
                service.procesarPedidos(List.of(p1, p2));

        assertEquals(2, response.getTotalProcesados());
        assertEquals(1, response.getGuardados());
        assertEquals(1, response.getConError());
    }

    @Test
    void debe_procesar_en_batches() {

        List<Pedido> pedidos = IntStream.range(0, 600)
                .mapToObj(i -> buildPedido("P" + i))
                .toList();

        mockCatalogosValidos();

        when(pedidoRepository.findAllByNumeroPedidoIn(any()))
                .thenReturn(Set.of());

        when(validator.validar(any(), any(), any()))
                .thenReturn(Optional.empty());

        service.procesarPedidos(pedidos);

        // 🔥 debe guardar en al menos 2 batches (500 + 100)
        verify(pedidoRepository, atLeast(2)).saveAll(any());
    }
}