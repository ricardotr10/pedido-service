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
    
    private final IdempotenciaRepository idempotenciaRepository;
    
    @Override
    public Optional<Idempotencia> findByIdempotencyKeyAndArchivoHash(String key, String hash) {
        return idempotenciaRepository.findByIdempotencyKeyAndArchivoHash(key, hash)
            .map(this::toDomain);
    }
    
    @Override
    public Idempotencia save(Idempotencia idempotencia) {
        IdempotenciaEntity entity = toEntity(idempotencia);
        IdempotenciaEntity saved = idempotenciaRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public boolean existsByIdempotencyKeyAndArchivoHash(String key, String hash) {
        return idempotenciaRepository.existsByIdempotencyKeyAndArchivoHash(key, hash);
    }
    
    private IdempotenciaEntity toEntity(Idempotencia domain) {
        return IdempotenciaEntity.builder()
            .id(domain.getId())
            .idempotencyKey(domain.getIdempotencyKey())
            .archivoHash(domain.getArchivoHash())
            .build();
    }
    
    private Idempotencia toDomain(IdempotenciaEntity entity) {
        return Idempotencia.builder()
            .id(entity.getId())
            .idempotencyKey(entity.getIdempotencyKey())
            .archivoHash(entity.getArchivoHash())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}