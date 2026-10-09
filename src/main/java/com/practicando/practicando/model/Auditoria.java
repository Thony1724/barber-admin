
package com.practicando.practicando.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "auditorias",
    indexes = {
        @Index(
            name = "idx_auditorias_fecha",
            columnList = "fecha"
        )
    }
)
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 60)
    private String accion;

    @Column(nullable = false, length = 60)
    private String modulo;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @PrePersist
    public void asignarFecha() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
