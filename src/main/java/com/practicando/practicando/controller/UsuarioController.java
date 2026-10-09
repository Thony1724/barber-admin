
package com.practicando.practicando.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

import com.practicando.practicando.dto.UsuarioDTO;
import com.practicando.practicando.model.Rol;
import com.practicando.practicando.model.Usuario;
import com.practicando.practicando.service.UsuarioService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.practicando.practicando.dto.RestablecerPasswordDTO;

import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(
            Model model,
            Authentication authentication) {

        model.addAttribute(
                "usuarios",
                usuarioService.listarTodos());

        model.addAttribute(
                "usuarioActual",
                authentication.getName());

        return "usuarios";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("usuario", new UsuarioDTO());
        model.addAttribute("roles", Rol.values());

        return "usuarios-form";
    }

    @PostMapping
    public String guardar(
            @Valid @ModelAttribute("usuario") UsuarioDTO dto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            return "usuarios-form";
        }

        try {
            usuarioService.crearUsuario(dto);

        } catch (IllegalArgumentException e) {

            model.addAttribute("errorGeneral", e.getMessage());
            model.addAttribute("roles", Rol.values());

            return "usuarios-form";
        }

        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            usuarioService.desactivarUsuario(
                    id,
                    authentication.getName());

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Usuario desactivado correctamente");

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    e.getMessage());
        }

        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/activar")
    public String activar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            usuarioService.activarUsuario(id);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Usuario activado correctamente");

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    e.getMessage());
        }

        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/password")
    public String formularioRestablecer(
            @PathVariable Long id,
            Model model) {

        Usuario usuario = usuarioService.buscarPorId(id);

        model.addAttribute("usuarioId", usuario.getId());
        model.addAttribute("username", usuario.getUsername());
        model.addAttribute(
                "passwordDTO",
                new RestablecerPasswordDTO());

        return "usuarios-password";
    }

    @PostMapping("/{id}/password")
    public String restablecerPassword(
            @PathVariable Long id,
            @Valid @ModelAttribute("passwordDTO") RestablecerPasswordDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioService.buscarPorId(id);

        if (!dto.getPasswordNueva().equals(
                dto.getConfirmarPassword())) {

            result.rejectValue(
                    "confirmarPassword",
                    "password.noCoincide",
                    "Las contraseñas no coinciden");
        }

        if (result.hasErrors()) {

            model.addAttribute("usuarioId", usuario.getId());
            model.addAttribute("username", usuario.getUsername());

            return "usuarios-password";
        }

        usuarioService.restablecerPassword(
                id,
                dto.getPasswordNueva());

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "Contraseña restablecida correctamente");

        return "redirect:/usuarios";
    }

}
