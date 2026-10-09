
package com.practicando.practicando.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.practicando.practicando.model.Auditoria;
import com.practicando.practicando.service.AuditoriaService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.practicando.practicando.service.AuditoriaExcelService;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;
    private final AuditoriaExcelService auditoriaExcelService;

    public AuditoriaController(
            AuditoriaService auditoriaService,
            AuditoriaExcelService auditoriaExcelService) {

        this.auditoriaService = auditoriaService;
        this.auditoriaExcelService = auditoriaExcelService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String accion,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,

            @RequestParam(defaultValue = "0") int pagina,
            Model model) {

        Page<Auditoria> resultados;

        try {

            resultados = auditoriaService.buscarConFiltros(
                    username,
                    accion,
                    fechaInicio,
                    fechaFin,
                    pagina);

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "mensajeError",
                    e.getMessage());

            resultados = auditoriaService.buscarConFiltros(
                    username,
                    accion,
                    null,
                    null,
                    0);
        }

        model.addAttribute(
                "actividades",
                resultados.getContent());

        model.addAttribute("paginaActual", resultados.getNumber());
        model.addAttribute("totalPaginas", resultados.getTotalPages());
        model.addAttribute("totalRegistros", resultados.getTotalElements());

        model.addAttribute("username", username);
        model.addAttribute("accion", accion);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);

        return "auditoria";
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String accion,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin)
            throws IOException {

        List<Auditoria> registros = auditoriaService.obtenerParaExportar(
                username,
                accion,
                fechaInicio,
                fechaFin);

        byte[] archivo = auditoriaExcelService.generarExcel(registros);

        String fechaArchivo = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(
                        "yyyyMMdd_HHmmss"));

        String nombreArchivo = "auditoria_" + fechaArchivo + ".xlsx";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                nombreArchivo + "\"")
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(archivo.length)
                .body(archivo);
    }

}
