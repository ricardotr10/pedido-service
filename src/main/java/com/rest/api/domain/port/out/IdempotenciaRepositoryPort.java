package com.rest.api.domain.port.out;

import com.rest.api.domain.model.Idempotencia;
import java.util.Optional;

public interface IdempotenciaRepositoryPort {
    Optional<Idempotencia> findByIdempotencyKeyAndArchivoHash(String key, String hash);
    Idempotencia save(Idempotencia idempotencia);
    boolean existsByIdempotencyKeyAndArchivoHash(String key, String hash);
}