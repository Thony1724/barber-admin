
package com.practicando.practicando.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.practicando.practicando.model.Auditoria;
import com.practicando.practicando.repository.AuditoriaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

import com.practicando.practicando.dto.AuditoriaEstadisticaDTO;
import java.time.LocalDate;



@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    

    public AuditoriaService(
            AuditoriaRepository auditoriaRepository) {

        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public void registrar(
            String accion,
            String modulo,
            String descripcion) {

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        String username = "SISTEMA";

        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(
                        authentication.getPrincipal())) {

            username = authentication.getName();
        }

        Auditoria auditoria = new Auditoria();

        auditoria.setUsername(username);
        auditoria.setAccion(accion);
        auditoria.setModulo(modulo);
        auditoria.setDescripcion(descripcion);

        auditoriaRepository.save(auditoria);
    }

    @Transactional(readOnly = true)
    public Page<Auditoria> buscarConFiltros(
            String username,
            String accion,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            int pagina) {

        if (fechaInicio != null
                && fechaFin != null
                && fechaInicio.isAfter(fechaFin)) {

            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser mayor que la final");
        }

        Specification<Auditoria> filtros = (root, query, cb) -> cb.conjunction();

        if (username != null && !username.isBlank()) {

            String usuarioBuscado = username.trim().toLowerCase();

            filtros = filtros.and(
                    (root, query, cb) -> cb.like(
                            cb.lower(root.get("username")),
                            "%" + usuarioBuscado + "%"));
        }

        if (accion != null && !accion.isBlank()) {

            filtros = filtros.and(
                    (root, query, cb) -> cb.equal(
                            root.get("accion"),
                            accion));
        }

        if (fechaInicio != null) {

            LocalDateTime inicio = fechaInicio.atStartOfDay();

            filtros = filtros.and(
                    (root, query, cb) -> cb.greaterThanOrEqualTo(
                            root.get("fecha"),
                            inicio));
        }

        if (fechaFin != null) {

            LocalDateTime finExclusivo = fechaFin.plusDays(1).atStartOfDay();

            filtros = filtros.and(
                    (root, query, cb) -> cb.lessThan(
                            root.get("fecha"),
                            finExclusivo));
        }

        Pageable pageable = PageRequest.of(
                Math.max(pagina, 0),
                10,
                Sort.by(
                        Sort.Order.desc("fecha"),
                        Sort.Order.desc("id")));

        return auditoriaRepository.findAll(
                filtros,
                pageable);
    }

    @Transactional(readOnly = true)
    public List<Auditoria> obtenerParaExportar(
            String username,
            String accion,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        List<Auditoria> registros = new ArrayList<>();

        // Límite de seguridad para evitar exportaciones excesivas.
        final int maxRegistros = 10000;

        int pagina = 0;

        Page<Auditoria> resultados;

        do {

            resultados = buscarConFiltros(
                    username,
                    accion,
                    fechaInicio,
                    fechaFin,
                    pagina);

            int disponibles = maxRegistros - registros.size();

            if (disponibles <= 0) {
                break;
            }

            List<Auditoria> contenido = resultados.getContent();

            registros.addAll(
                    contenido.subList(
                            0,
                            Math.min(contenido.size(), disponibles)));

            pagina++;

        } while (resultados.hasNext()
                && registros.size() < maxRegistros);

        return registros;
    }

    @Transactional(readOnly = true)
    public long contarTodasLasActividades() {

        return auditoriaRepository.count();
    }

    @Transactional(readOnly = true)
    public long contarActividadesHoy() {

        LocalDate hoy = LocalDate.now();

        return auditoriaRepository
                .countByFechaGreaterThanEqualAndFechaLessThan(
                        hoy.atStartOfDay(),
                        hoy.plusDays(1).atStartOfDay());
    }

    @Transactional(readOnly = true)
    public long contarUsuariosConActividadHoy() {

        LocalDate hoy = LocalDate.now();

        return auditoriaRepository.contarUsuariosConActividad(
                hoy.atStartOfDay(),
                hoy.plusDays(1).atStartOfDay());
    }

    @Transactional(readOnly = true)
    public List<AuditoriaEstadisticaDTO> obtenerEstadisticasPorAccion() {

        return auditoriaRepository.contarPorAccion();
    }

}
