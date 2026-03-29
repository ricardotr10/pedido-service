package com.rest.api.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Zona {
    private String id;
    private Boolean soporteRefrigeracion;
}