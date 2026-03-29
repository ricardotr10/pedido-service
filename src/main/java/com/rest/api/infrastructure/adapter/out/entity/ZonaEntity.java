package com.rest.api.infrastructure.adapter.out.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "zonas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZonaEntity {
    
    @Id
    private String id;
    
    @Column(name = "soporte_refrigeracion")
    private Boolean soporteRefrigeracion;
}