
package com.practicando.practicando.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;

import com.practicando.practicando.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {

        // 1. Obtener los pagos de una cita
        List<Pago> findByCitaIdOrderByFechaPagoDesc(Long citaId);

        // 2. Calcular el total pagado de una cita
        @Query("""
                        SELECT COALESCE(SUM(p.monto), 0)
                        FROM Pago p
                        WHERE p.cita.id = :citaId
                        """)
        BigDecimal calcularTotalPagadoPorCita(
                        @Param("citaId") Long citaId);

        // 3. Obtener todos los pagos con los datos de la cita y el cliente
        @EntityGraph(attributePaths = { "cita", "cita.cliente" })
        List<Pago> findAllByOrderByFechaPagoDesc();

        // 4. Calcular los ingresos registrados entre dos fechas
        @Query("""
                        SELECT COALESCE(SUM(p.monto), 0)
                        FROM Pago p
                        WHERE p.fechaPago >= :inicio
                          AND p.fechaPago < :fin
                        """)
        BigDecimal calcularIngresosEntreFechas(
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);

        @Query("""
                        SELECT p.metodoPago, SUM(p.monto)
                        FROM Pago p
                        WHERE p.fechaPago >= :inicio
                          AND p.fechaPago < :fin
                        GROUP BY p.metodoPago
                        ORDER BY p.metodoPago
                        """)
        List<Object[]> calcularIngresosPorMetodo(
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);

}
