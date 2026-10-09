package com.practicando.practicando.controller;

import com.practicando.practicando.dto.ServicioDTO;
import com.practicando.practicando.model.Servicio;
import com.practicando.practicando.service.ServicioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping("/servicios")
    public String listarServicios(Model model) {

        model.addAttribute(
                "servicios",
                servicioService.listarTodos()
        );

        return "servicios";
    }

    @GetMapping("/servicios/nuevo")
    public String mostrarFormulario(Model model) {

        model.addAttribute(
                "servicio",
                new ServicioDTO()
        );

        return "servicios-form";
    }

    @PostMapping("/servicios")
    public String guardarServicio(
            @Valid @ModelAttribute("servicio") ServicioDTO servicioDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "servicios-form";
        }

        servicioService.guardar(servicioDTO);

        return "redirect:/servicios";
    }

    @GetMapping("/servicios/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable Long id,
            Model model) {

        Servicio servicio = servicioService.buscarPorId(id);

        ServicioDTO servicioDTO = new ServicioDTO();

        servicioDTO.setNombre(servicio.getNombre());
        servicioDTO.setDescripcion(servicio.getDescripcion());
        servicioDTO.setPrecio(servicio.getPrecio());
        servicioDTO.setDuracionMinutos(servicio.getDuracionMinutos());

        model.addAttribute("servicio", servicioDTO);
        model.addAttribute("servicioId", id);

        return "servicios-form";
    }

    @PostMapping("/servicios/{id}")
    public String actualizarServicio(
            @PathVariable Long id,
            @Valid @ModelAttribute("servicio") ServicioDTO servicioDTO,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("servicioId", id);
            return "servicios-form";
        }

        servicioService.actualizar(id, servicioDTO);

        return "redirect:/servicios";
    }

    @PostMapping("/servicios/{id}/desactivar")
    public String desactivarServicio(@PathVariable Long id) {

        servicioService.desactivar(id);

        return "redirect:/servicios";
    }

    @PostMapping("/servicios/{id}/activar")
    public String activarServicio(@PathVariable Long id) {

        servicioService.activar(id);

        return "redirect:/servicios";
    }
}