package com.rest.api.infrastructure.adapter.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.api.application.dto.*;
import com.rest.api.application.service.PedidoBatchService;
import com.rest.api.domain.model.*;
import com.rest.api.domain.port.out.IdempotenciaRepositoryPort;
import com.rest.api.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoBatchService service;
    private final IdempotenciaRepositoryPort repo;
    private final HashUtil hashUtil;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping(value = "/cargar", consumes = "multipart/form-data")
    public ResponseEntity<CargaPedidosResponse> cargar(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Idempotency-Key") String key) throws Exception {

        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key requerido");
        }

        String hash = hashUtil.calcularHash(file.getBytes());

        Optional<Idempotencia> existente =
                repo.findByIdempotencyKeyAndArchivoHash(key, hash);

        if (existente.isPresent()) {
            return ResponseEntity.ok(
                    mapper.readValue(existente.get().getResponseJson(), CargaPedidosResponse.class)
            );
        }

        ParseResult parseResult = parseCsvSeguro(file);

        CargaPedidosResponse response =
                service.procesarPedidos(parseResult.validos);

        List<ErrorDetalle> todos = new ArrayList<>(response.getErrores());
        todos.addAll(parseResult.errores);

        response.setErrores(todos);
        response.setConError(todos.size());

        response.setTotalProcesados(
                parseResult.validos.size() + parseResult.errores.size()
        );

        Map<String, Long> agrupados = todos.stream()
                .collect(Collectors.groupingBy(
                        ErrorDetalle::getMotivo,
                        Collectors.counting()
                ));

        response.setErroresAgrupados(agrupados);

        // 🔥 Idempotencia segura (concurrencia)
        try {
            repo.save(Idempotencia.builder()
                    .id(UUID.randomUUID())
                    .idempotencyKey(key)
                    .archivoHash(hash)
                    .responseJson(mapper.writeValueAsString(response))
                    .build());
        } catch (Exception e) {
            Optional<Idempotencia> retry =
                    repo.findByIdempotencyKeyAndArchivoHash(key, hash);

            if (retry.isPresent()) {
                return ResponseEntity.ok(
                        mapper.readValue(retry.get().getResponseJson(), CargaPedidosResponse.class)
                );
            }
            throw e;
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private ParseResult parseCsvSeguro(MultipartFile file) throws Exception {

        List<Pedido> pedidos = new ArrayList<>();
        List<ErrorDetalle> errores = new ArrayList<>();

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {

            // 🔥 Validar headers
            List<String> headers = parser.getHeaderNames();

            List<String> expected = List.of(
                    "numeroPedido",
                    "clienteId",
                    "fechaEntrega",
                    "estado",
                    "zonaEntrega",
                    "requiereRefrigeracion"
            );

            if (!headers.containsAll(expected)) {
                throw new IllegalArgumentException("CSV inválido: columnas incorrectas");
            }

            int linea = 2;

            for (CSVRecord r : parser) {

                try {
                    Pedido.EstadoPedido estado;

                    try {
                        estado = Pedido.EstadoPedido.valueOf(r.get("estado"));
                    } catch (Exception e) {
                        estado = null;
                    }

                    pedidos.add(Pedido.builder()
                            .id(UUID.randomUUID())
                            .numeroPedido(r.get("numeroPedido"))
                            .clienteId(r.get("clienteId"))
                            .fechaEntrega(LocalDate.parse(r.get("fechaEntrega")))
                            .estado(estado)
                            .zonaId(r.get("zonaEntrega"))
                            .requiereRefrigeracion(Boolean.parseBoolean(r.get("requiereRefrigeracion")))
                            .build());

                } catch (Exception e) {
                    errores.add(ErrorDetalle.builder()
                            .linea(linea)
                            .motivo("FORMATO_INVALIDO")
                            .build());
                }

                linea++;
            }
        }

        return new ParseResult(pedidos, errores);
    }

    private record ParseResult(List<Pedido> validos, List<ErrorDetalle> errores) {}
}