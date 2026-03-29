package com.rest.api.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDetalle {
    private Integer linea;
    private String motivo;
}