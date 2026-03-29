package com.rest.api.infrastructure.adapter.out.repository.impl;

import com.rest.api.domain.model.Cliente;
import com.rest.api.domain.port.out.ClienteRepositoryPort;
import com.rest.api.infrastructure.adapter.out.entity.ClienteEntity;
import com.rest.api.infrastructure.adapter.out.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {
    
    private final ClienteRepository clienteRepository;
    
    @Override
    public Optional<Cliente> findById(String id) {
        return clienteRepository.findById(id).map(this::toDomain);
    }
    
    @Override
    public boolean existsById(String id) {
        return clienteRepository.existsById(id);
    }
    
    private Cliente toDomain(ClienteEntity entity) {
        return Cliente.builder()
            .id(entity.getId())
            .activo(entity.getActivo())
            .build();
    }
}