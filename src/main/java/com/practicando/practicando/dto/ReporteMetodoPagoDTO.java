
package com.practicando.practicando.dto;

import java.math.BigDecimal;

import com.practicando.practicando.model.MetodoPago;

public record ReporteMetodoPagoDTO(
        MetodoPago metodoPago,
        BigDecimal total
) {
}
