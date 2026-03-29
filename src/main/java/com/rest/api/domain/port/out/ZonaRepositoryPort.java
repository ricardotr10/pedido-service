package com.rest.api.domain.port.out;

import com.rest.api.domain.model.Zona;

import java.util.List;
import java.util.Optional;

public interface ZonaRepositoryPort {
    Optional<Zona> findById(String id);
    boolean existsById(String id);

    // ✅ NUEVO
    List<Zona> findAllByIds(List<String> ids);
}