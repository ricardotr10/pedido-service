package com.rest.api.infrastructure.adapter.in;

import com.rest.api.application.dto.CargaPedidosResponse;
import com.rest.api.application.service.PedidoBatchService;
import com.rest.api.domain.model.Idempotencia;
import com.rest.api.domain.model.Pedido;
import com.rest.api.domain.port.out.IdempotenciaRepositoryPort;
import com.rest.api.util.HashUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Endpoint para carga de pedidos")
public class PedidoController {
    
    private final PedidoBatchService pedidoBatchService;
    private final IdempotenciaRepositoryPort idempotenciaRepository;
    private final HashUtil hashUtil;
    
    @PostMapping(value = "/cargar", consumes = "multipart/form-data")
    @Operation(summary = "Cargar pedidos desde archivo CSV")
    public ResponseEntity<CargaPedidosResponse> cargarPedidos(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Idempotency-Key") String idempotencyKey) throws Exception {
        
        log.info("Recibiendo carga de pedidos - IdempotencyKey: {}", idempotencyKey);
        
        String fileHash = hashUtil.calcularHash(file.getBytes());
        
        if (idempotenciaRepository.existsByIdempotencyKeyAndArchivoHash(idempotencyKey, fileHash)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CargaPedidosResponse.builder()
                    .totalProcesados(0)
                    .guardados(0)
                    .conError(0)
                    .errores(new ArrayList<>())
                    .erroresAgrupados(new java.util.HashMap<>())
                    .build());
        }
        
        List<Pedido> pedidos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line;
            boolean isFirstLine = true;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] fields = line.split(",");
                if (fields.length < 6) continue;
                
                Pedido pedido = Pedido.builder()
                    .id(UUID.randomUUID())
                    .numeroPedido(fields[0].trim())
                    .clienteId(fields[1].trim())
                    .fechaEntrega(LocalDate.parse(fields[2].trim(), formatter))
                    .estado(Pedido.EstadoPedido.valueOf(fields[3].trim()))
                    .zonaId(fields[4].trim())
                    .requiereRefrigeracion(Boolean.parseBoolean(fields[5].trim()))
                    .build();
                
                pedidos.add(pedido);
            }
        }
        
        CargaPedidosResponse response = pedidoBatchService.procesarPedidos(pedidos);
        
        Idempotencia idempotencia = Idempotencia.builder()
            .id(UUID.randomUUID())
            .idempotencyKey(idempotencyKey)
            .archivoHash(fileHash)
            .build();
        idempotenciaRepository.save(idempotencia);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}