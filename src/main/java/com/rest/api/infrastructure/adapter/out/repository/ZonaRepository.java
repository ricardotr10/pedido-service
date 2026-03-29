package com.rest.api.infrastructure.adapter.out.repository;

import com.rest.api.infrastructure.adapter.out.entity.ZonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZonaRepository extends JpaRepository<ZonaEntity, String> {
}