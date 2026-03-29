package com.rest.api.infrastructure.adapter.out.repository.impl;

import com.rest.api.domain.model.Idempotencia;
import com.rest.api.domain.port.out.IdempotenciaRepositoryPort;
import com.rest.api.infrastructure.adapter.out.entity.IdempotenciaEntity;
import com.rest.api.infrastructure.adapter.out.repository.IdempotenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IdempotenciaRepositoryAdapter implements IdempotenciaRepositoryPort {

    private final IdempotenciaRepository repository;

    @Override
    public Optional<Idempotencia> findByIdempotencyKeyAndArchivoHash(String key, String hash) {
        return repository.findByIdempotencyKeyAndArchivoHash(key, hash)
                .map(this::toDomain);
    }

    @Override
    public Idempotencia save(Idempotencia idempotencia) {
        return toDomain(repository.save(toEntity(idempotencia)));
    }

    @Override
    public boolean existsByIdempotencyKeyAndArchivoHash(String key, String hash) {
        return repository.existsByIdempotencyKeyAndArchivoHash(key, hash);
    }

    private IdempotenciaEntity toEntity(Idempotencia d) {
        return IdempotenciaEntity.builder()
                .id(d.getId())
                .idempotencyKey(d.getIdempotencyKey())
                .archivoHash(d.getArchivoHash())
                .responseJson(d.getResponseJson())
                .build();
    }

    private Idempotencia toDomain(IdempotenciaEntity e) {
        return Idempotencia.builder()
                .id(e.getId())
                .idempotencyKey(e.getIdempotencyKey())
                .archivoHash(e.getArchivoHash())
                .responseJson(e.getResponseJson())
                .createdAt(e.getCreatedAt())
                .build();
    }
}