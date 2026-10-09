package com.practicando.practicando.dto;

import java.math.BigDecimal;

import com.practicando.practicando.model.MetodoPago;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagoDTO {

    @NotNull(message = "Debe seleccionar una cita")
    private Long citaId;

    @NotNull(message = "Ingrese el monto del pago")
    @DecimalMin(
        value = "0.01",
        message = "El monto debe ser mayor que cero"
    )
    private BigDecimal monto;

    @NotNull(message = "Seleccione un método de pago")
    private MetodoPago metodoPago;

}
