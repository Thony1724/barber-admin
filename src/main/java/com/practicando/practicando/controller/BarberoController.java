package com.practicando.practicando.controller;

import com.practicando.practicando.dto.BarberoDTO;
import com.practicando.practicando.exception.EmailDuplicadoException;
import com.practicando.practicando.model.Barbero;
import com.practicando.practicando.service.BarberoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BarberoController {

    private final BarberoService barberoService;

    public BarberoController(BarberoService barberoService) {
        this.barberoService = barberoService;
    }

    @GetMapping("/barberos")
    public String listarBarberos(Model model) {

        model.addAttribute(
                "barberos",
                barberoService.listarTodos());

        return "barberos";
    }

    @GetMapping("/barberos/nuevo")
    public String mostrarFormulario(Model model) {

        model.addAttribute(
                "barbero",
                new BarberoDTO());

        return "barberos-form";
    }

    @PostMapping("/barberos")
    public String guardarBarbero(
            @Valid @ModelAttribute("barbero") BarberoDTO barberoDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "barberos-form";
        }

        try {

            barberoService.guardar(barberoDTO);

        } catch (EmailDuplicadoException e) {

            bindingResult.rejectValue(
                    "email",
                    "email.duplicado",
                    e.getMessage());

            return "barberos-form";
        }

        return "redirect:/barberos";
    }

    @GetMapping("/barberos/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable Long id,
            Model model) {

        Barbero barbero = barberoService.buscarPorId(id);

        BarberoDTO barberoDTO = new BarberoDTO();

        barberoDTO.setNombre(barbero.getNombre());
        barberoDTO.setApellido(barbero.getApellido());
        barberoDTO.setTelefono(barbero.getTelefono());
        barberoDTO.setEmail(barbero.getEmail());
        barberoDTO.setEspecialidad(barbero.getEspecialidad());

        model.addAttribute("barbero", barberoDTO);
        model.addAttribute("barberoId", id);

        return "barberos-form";
    }

    @PostMapping("/barberos/{id}")
    public String actualizarBarbero(
            @PathVariable Long id,
            @Valid @ModelAttribute("barbero") BarberoDTO barberoDTO,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("barberoId", id);
            return "barberos-form";
        }

        try {

            barberoService.actualizar(id, barberoDTO);

        } catch (EmailDuplicadoException e) {

            bindingResult.rejectValue(
                    "email",
                    "email.duplicado",
                    e.getMessage());

            model.addAttribute("barberoId", id);

            return "barberos-form";
        }

        return "redirect:/barberos";
    }

    @PostMapping("/barberos/{id}/desactivar")
    public String desactivarBarbero(@PathVariable Long id) {

        barberoService.desactivar(id);

        return "redirect:/barberos";
    }

    @PostMapping("/barberos/{id}/activar")
    public String activarBarbero(@PathVariable Long id) {

        barberoService.activar(id);

        return "redirect:/barberos";
    }
}