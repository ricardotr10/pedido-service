package com.rest.api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor  // ← Agregar esto
@AllArgsConstructor // ← Agregar esto
public class CargaPedidosResponse {
    private Integer totalProcesados;
    private Integer guardados;
    private Integer conError;
    private List<ErrorDetalle> errores;
    private Map<String, Long> erroresAgrupados;
}