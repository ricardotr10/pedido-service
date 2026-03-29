package com.rest.api.domain.port.out;

import com.rest.api.domain.model.Zona;
import java.util.Optional;

public interface ZonaRepositoryPort {
    Optional<Zona> findById(String id);
    boolean existsById(String id);
}