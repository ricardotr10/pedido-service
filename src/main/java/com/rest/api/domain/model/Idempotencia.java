package com.rest.api.domain.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Idempotencia {
    private UUID id;
    private String idempotencyKey;
    private String archivoHash;
    private String responseJson;
    private LocalDateTime createdAt;
}