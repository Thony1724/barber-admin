package com.practicando.practicando.dto;

import com.practicando.practicando.model.EstadoCita;

import java.time.LocalTime;

public record CitaAgendaDTO(
        Long id,
        String cliente,
        Long barberoId,
        String barbero,
        String servicio,
        LocalTime horaInicio,
        LocalTime horaFin,
        EstadoCita estado
) {
}