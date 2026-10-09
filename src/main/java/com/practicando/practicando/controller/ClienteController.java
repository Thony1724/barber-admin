package com.practicando.practicando.controller;

import com.practicando.practicando.dto.ClienteDTO;
import com.practicando.practicando.service.ClienteService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.practicando.practicando.model.Cliente;
import org.springframework.web.bind.annotation.PathVariable;
import com.practicando.practicando.exception.EmailDuplicadoException;

@Controller
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/clientes")
    public String listarClientes(Model model) {

        model.addAttribute(
                "clientes",
                clienteService.listarTodos());
        model.addAttribute("seccionActiva", "clientes");
        return "clientes";
    }

    @GetMapping("/clientes/nuevo")
    public String mostrarFormulario(Model model) {

        model.addAttribute(
                "cliente",
                new ClienteDTO());

        return "clientes-form";
    }

    @PostMapping("/clientes")
    public String guardarCliente(
            @Valid @ModelAttribute("cliente") ClienteDTO clienteDTO,
            BindingResult bindingResult) {

        // 1. Revisamos las validaciones del DTO
        if (bindingResult.hasErrors()) {
            return "clientes-form";
        }

        try {

            // 2. Intentamos guardar el cliente
            clienteService.guardar(clienteDTO);

        } catch (EmailDuplicadoException e) {

            // 3. Asociamos el error específicamente al campo email
            bindingResult.rejectValue(
                    "email",
                    "email.duplicado",
                    e.getMessage());

            return "clientes-form";
        }

        // 4. Si todo salió correctamente, volvemos al listado
        return "redirect:/clientes";
    }

    @GetMapping("/clientes/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable Long id,
            Model model) {

        Cliente cliente = clienteService.buscarPorId(id);

        ClienteDTO clienteDTO = new ClienteDTO();

        clienteDTO.setNombre(cliente.getNombre());
        clienteDTO.setApellido(cliente.getApellido());
        clienteDTO.setTelefono(cliente.getTelefono());
        clienteDTO.setEmail(cliente.getEmail());

        model.addAttribute("cliente", clienteDTO);
        model.addAttribute("clienteId", id);

        return "clientes-form";
    }

    @PostMapping("/clientes/{id}")
    public String actualizarCliente(
            @PathVariable Long id,
            @Valid @ModelAttribute("cliente") ClienteDTO clienteDTO,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("clienteId", id);
            return "clientes-form";
        }

        try {

            clienteService.actualizar(id, clienteDTO);

        } catch (EmailDuplicadoException e) {

            bindingResult.rejectValue(
                    "email",
                    "email.duplicado",
                    e.getMessage());

            model.addAttribute("clienteId", id);

            return "clientes-form";
        }

        return "redirect:/clientes";
    }

    @PostMapping("/clientes/{id}/eliminar")
    public String eliminarCliente(@PathVariable Long id) {

        clienteService.eliminar(id);

        return "redirect:/clientes";
    }
}