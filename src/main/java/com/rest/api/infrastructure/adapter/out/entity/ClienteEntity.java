package com.rest.api.infrastructure.adapter.out.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntity {
    
    @Id
    private String id;
    
    private Boolean activo;
}