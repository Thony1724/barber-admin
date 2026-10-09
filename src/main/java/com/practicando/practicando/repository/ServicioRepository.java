package com.practicando.practicando.repository;

import com.practicando.practicando.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    List<Servicio> findByActivoTrue();

    long countByActivoTrue();
}