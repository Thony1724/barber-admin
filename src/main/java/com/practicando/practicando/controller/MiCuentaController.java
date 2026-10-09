
package com.practicando.practicando.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.practicando.practicando.dto.CambiarPasswordDTO;
import com.practicando.practicando.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/mi-cuenta")
public class MiCuentaController {

    private final UsuarioService usuarioService;

    public MiCuentaController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String formulario(
            Model model,
            Authentication authentication) {

        model.addAttribute(
                "username",
                authentication.getName()
        );

        model.addAttribute(
                "passwordDTO",
                new CambiarPasswordDTO()
        );

        return "mi-cuenta";
    }

    @PostMapping("/password")
    public String cambiarPassword(
            @Valid @ModelAttribute("passwordDTO")
            CambiarPasswordDTO dto,
            BindingResult result,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute(
                    "username",
                    authentication.getName()
            );
            return "mi-cuenta";
        }

        try {

            usuarioService.cambiarPassword(
                    authentication.getName(),
                    dto
            );

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Contraseña actualizada correctamente"
            );

            return "redirect:/mi-cuenta";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "username",
                    authentication.getName()
            );

            model.addAttribute(
                    "mensajeError",
                    e.getMessage()
            );

            return "mi-cuenta";
        }
    }
}
