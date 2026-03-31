package com.rest.api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor  // ← Agregar esto
@AllArgsConstructor // ← Agregar esto
public class ErrorDetalle {
    private Integer linea;
    private String motivo;
}