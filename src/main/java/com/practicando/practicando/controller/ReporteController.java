
package com.practicando.practicando.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.practicando.practicando.dto.ReporteMetodoPagoDTO;
import com.practicando.practicando.service.PagoService;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final PagoService pagoService;

    public ReporteController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public String mostrarReportes(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaFin,

            Model model) {

        LocalDate hoy = LocalDate.now();

        if (fechaInicio == null) {
            fechaInicio = hoy.withDayOfMonth(1);
        }

        if (fechaFin == null) {
            fechaFin = hoy;
        }

        if (fechaInicio.isAfter(fechaFin)) {
            model.addAttribute(
                    "error",
                    "La fecha inicial no puede ser mayor que la final"
            );

            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);

            return "reportes";
        }

        YearMonth mesActual = YearMonth.from(hoy);

        BigDecimal ingresosHoy =
                pagoService.calcularIngresosPorFecha(hoy);

        BigDecimal ingresosMes =
                pagoService.calcularIngresosDelMes(
                        mesActual.getYear(),
                        mesActual.getMonthValue()
                );

        List<ReporteMetodoPagoDTO> ingresosPorMetodo =
                pagoService.obtenerIngresosPorMetodo(
                        fechaInicio,
                        fechaFin
                );

        BigDecimal ingresosPeriodo = ingresosPorMetodo.stream()
                .map(ReporteMetodoPagoDTO::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("ingresosHoy", ingresosHoy);
        model.addAttribute("ingresosMes", ingresosMes);
        model.addAttribute("ingresosPeriodo", ingresosPeriodo);

        model.addAttribute("ingresosPorMetodo", ingresosPorMetodo);

        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);

        return "reportes";
    }
}
