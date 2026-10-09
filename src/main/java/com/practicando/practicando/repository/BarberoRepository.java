package com.practicando.practicando.repository;

import com.practicando.practicando.model.Barbero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarberoRepository extends JpaRepository<Barbero, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<Barbero> findByActivoTrue();

    long countByActivoTrue();
}