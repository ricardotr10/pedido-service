package com.rest.api.application.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ErrorAgrupado {
    private String tipo;
    private Long cantidad;
    private List<Integer> lineas;
}