package com.practicando.practicando.repository;

import com.practicando.practicando.model.Cita;
import com.practicando.practicando.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.LocalDate;
import java.util.List;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CitaRepository extends JpaRepository<Cita, Long> {

        List<Cita> findByBarberoIdAndFechaAndEstadoNot(
                        Long barberoId,
                        LocalDate fecha,
                        EstadoCita estado);

        @EntityGraph(attributePaths = { "cliente", "barbero", "servicio" })
        List<Cita> findAllByOrderByFechaAscHoraAsc();

        @EntityGraph(attributePaths = {
                        "cliente",
                        "barbero",
                        "servicio"
        })
        List<Cita> findByFechaOrderByHoraAsc(LocalDate fecha);

        long countByFechaAndEstadoNot(
                        LocalDate fecha,
                        EstadoCita estado);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT c FROM Cita c WHERE c.id = :id")
        Optional<Cita> buscarPorIdConBloqueo(
                        @Param("id") Long id);

}