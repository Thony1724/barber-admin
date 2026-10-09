
package com.practicando.practicando.dto;

import java.math.BigDecimal;

import com.practicando.practicando.model.EstadoPago;

public record ResumenPagoDTO(

        Long citaId,
        String cliente,
        String servicio,
        BigDecimal precio,
        BigDecimal totalPagado,
        BigDecimal saldoPendiente,
        EstadoPago estadoPago

) {
}
