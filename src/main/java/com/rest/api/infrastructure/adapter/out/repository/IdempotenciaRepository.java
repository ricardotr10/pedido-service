package com.rest.api.infrastructure.adapter.out.repository;

import com.rest.api.infrastructure.adapter.out.entity.IdempotenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdempotenciaRepository extends JpaRepository<IdempotenciaEntity, UUID> {
    Optional<IdempotenciaEntity> findByIdempotencyKeyAndArchivoHash(String idempotencyKey, String archivoHash);
    boolean existsByIdempotencyKeyAndArchivoHash(String idempotencyKey, String archivoHash);
}