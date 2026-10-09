package com.practicando.practicando.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CitaDTO {

    @NotNull(message = "Debe seleccionar un cliente")
    private Long clienteId;

    @NotNull(message = "Debe seleccionar un barbero")
    private Long barberoId;

    @NotNull(message = "Debe seleccionar un servicio")
    private Long servicioId;

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha no puede ser anterior a hoy")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;
}