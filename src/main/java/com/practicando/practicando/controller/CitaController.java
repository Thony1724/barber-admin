package com.practicando.practicando.controller;

import com.practicando.practicando.dto.CitaDTO;
import com.practicando.practicando.exception.CitaInvalidaException;
import com.practicando.practicando.service.*;

import jakarta.validation.Valid;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;
    private final ClienteService clienteService;
    private final BarberoService barberoService;
    private final ServicioService servicioService;

    public CitaController(
            CitaService citaService,
            ClienteService clienteService,
            BarberoService barberoService,
            ServicioService servicioService) {

        this.citaService = citaService;
        this.clienteService = clienteService;
        this.barberoService = barberoService;
        this.servicioService = servicioService;
    }

    // Listar citas
    @GetMapping
    public String listar(Model model) {

        model.addAttribute("citas", citaService.listarTodas());

        return "citas";
    }

    // Mostrar formulario
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {

        model.addAttribute("cita", new CitaDTO());

        cargarOpciones(model);

        return "citas-form";
    }

    // Guardar cita
    @PostMapping
    public String guardar(
            @Valid @ModelAttribute("cita") CitaDTO citaDTO,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            cargarOpciones(model);
            return "citas-form";
        }

        try {

            citaService.guardar(citaDTO);

        } catch (CitaInvalidaException e) {

            model.addAttribute("errorGeneral", e.getMessage());
            cargarOpciones(model);

            return "citas-form";
        }

        return "redirect:/citas";
    }

    // Cargar opciones para los desplegables
    private void cargarOpciones(Model model) {

        model.addAttribute(
                "clientes",
                clienteService.listarTodos());

        model.addAttribute(
                "barberos",
                barberoService.listarActivos());

        model.addAttribute(
                "servicios",
                servicioService.listarActivos());
    }

    @PostMapping("/{id}/confirmar")
    public String confirmar(@PathVariable Long id) {
        citaService.confirmar(id);
        return "redirect:/citas";
    }

    @PostMapping("/{id}/completar")
    public String completar(@PathVariable Long id) {
        citaService.completar(id);
        return "redirect:/citas";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id) {
        citaService.cancelar(id);
        return "redirect:/citas";
    }

    @GetMapping("/agenda")
    public String mostrarAgenda(
            @RequestParam(required = false) LocalDate fecha,
            Model model) {

        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();

        model.addAttribute(
                "agenda",
                citaService.obtenerAgenda(fechaConsulta));

        model.addAttribute("fechaSeleccionada", fechaConsulta);

        model.addAttribute(
                "barberos",
                barberoService.listarTodos());

        return "citas-agenda";
    }
}