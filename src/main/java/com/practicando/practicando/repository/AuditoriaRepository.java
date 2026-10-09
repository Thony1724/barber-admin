
package com.practicando.practicando.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.practicando.practicando.dto.AuditoriaEstadisticaDTO;
import com.practicando.practicando.model.Auditoria;

public interface AuditoriaRepository
        extends JpaRepository<Auditoria, Long>,
                JpaSpecificationExecutor<Auditoria> {

    long countByFechaGreaterThanEqualAndFechaLessThan(
            LocalDateTime inicio,
            LocalDateTime fin
    );

    @Query("""
        SELECT new com.practicando.practicando.dto.AuditoriaEstadisticaDTO(
            a.accion,
            COUNT(a)
        )
        FROM Auditoria a
        GROUP BY a.accion
        ORDER BY COUNT(a) DESC
    """)
    List<AuditoriaEstadisticaDTO> contarPorAccion();

    @Query("""
        SELECT COUNT(DISTINCT a.username)
        FROM Auditoria a
        WHERE a.fecha >= :inicio
        AND a.fecha < :fin
    """)
    long contarUsuariosConActividad(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
}
