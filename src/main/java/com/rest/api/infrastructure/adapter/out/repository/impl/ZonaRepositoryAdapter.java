package com.rest.api.infrastructure.adapter.out.repository.impl;

import com.rest.api.domain.model.Zona;
import com.rest.api.domain.port.out.ZonaRepositoryPort;
import com.rest.api.infrastructure.adapter.out.entity.ZonaEntity;
import com.rest.api.infrastructure.adapter.out.repository.ZonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ZonaRepositoryAdapter implements ZonaRepositoryPort {
    
    private final ZonaRepository zonaRepository;
    
    @Override
    public List<Zona> findAllByIds(List<String> ids) {
        return zonaRepository.findAllById(ids)
                .stream()
                .map(this::toDomain)
                .toList();
    }
    
    @Override
    public Optional<Zona> findById(String id) {
        return zonaRepository.findById(id).map(this::toDomain);
    }
    
    @Override
    public boolean existsById(String id) {
        return zonaRepository.existsById(id);
    }
    
    private Zona toDomain(ZonaEntity entity) {
        return Zona.builder()
            .id(entity.getId())
            .soporteRefrigeracion(entity.getSoporteRefrigeracion())
            .build();
    }
}