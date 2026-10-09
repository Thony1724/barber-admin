package com.practicando.practicando.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelController {

    @ModelAttribute("seccionActiva")
    public String obtenerSeccionActiva(HttpServletRequest request) {

        String ruta = request.getServletPath();

        if (ruta.startsWith("/citas/agenda")) {
            return "agenda";
        }

        if (ruta.startsWith("/clientes")) {
            return "clientes";
        }

        if (ruta.startsWith("/barberos")) {
            return "barberos";
        }

        if (ruta.startsWith("/servicios")) {
            return "servicios";
        }

        if (ruta.startsWith("/citas")) {
            return "citas";
        }

        if (ruta.startsWith("/pagos")) {
            return "pagos";
        }

        if (ruta.startsWith("/reportes")) {
            return "reportes";
        }

        if (ruta.startsWith("/usuarios")) {
            return "usuarios";
        }

        if (ruta.startsWith("/mi-cuenta")) {
            return "mi-cuenta";
        }

        if (ruta.startsWith("/auditoria")) {
            return "auditoria";
        }

        return "dashboard";
    }
}