package com.rest.api.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Cliente {
    private String id;
    private Boolean activo;
}