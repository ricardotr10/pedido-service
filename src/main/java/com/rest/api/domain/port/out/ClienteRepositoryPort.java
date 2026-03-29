package com.rest.api.domain.port.out;

import com.rest.api.domain.model.Cliente;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Optional<Cliente> findById(String id);
    boolean existsById(String id);
}