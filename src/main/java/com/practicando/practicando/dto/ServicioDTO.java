package com.practicando.practicando.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServicioDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(
        value = "0.01",
        message = "El precio debe ser mayor a 0"
    )
    private BigDecimal precio;

    @NotNull(message = "La duración es obligatoria")
    @Min(
        value = 1,
        message = "La duración debe ser mayor a 0 minutos"
    )
    private Integer duracionMinutos;
}
