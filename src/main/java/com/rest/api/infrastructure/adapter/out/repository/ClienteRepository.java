package com.rest.api.infrastructure.adapter.out.repository;

import com.rest.api.infrastructure.adapter.out.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, String> {
}