
package com.practicando.practicando.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.practicando.practicando.dto.AuditoriaEstadisticaDTO;
import com.practicando.practicando.service.AuditoriaService;

@Controller
@RequestMapping("/auditoria/dashboard")
public class AuditoriaDashboardController {

    private final AuditoriaService auditoriaService;

    public AuditoriaDashboardController(
            AuditoriaService auditoriaService) {

        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public String dashboard(Model model) {

        model.addAttribute(
                "totalActividades",
                auditoriaService.contarTodasLasActividades()
        );

        model.addAttribute(
                "actividadesHoy",
                auditoriaService.contarActividadesHoy()
        );

        model.addAttribute(
                "usuariosConActividadHoy",
                auditoriaService.contarUsuariosConActividadHoy()
        );

        List<AuditoriaEstadisticaDTO> estadisticas =
                auditoriaService.obtenerEstadisticasPorAccion();

        model.addAttribute("estadisticas", estadisticas);

        model.addAttribute(
                "accionMasFrecuente",
                estadisticas.isEmpty()
                        ? "SIN REGISTROS"
                        : estadisticas.get(0).accion()
        );

        return "auditoria-dashboard";
    }
}
