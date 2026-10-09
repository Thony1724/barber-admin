
package com.practicando.practicando.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.practicando.practicando.dto.PagoDTO;
import com.practicando.practicando.exception.PagoInvalidoException;
import com.practicando.practicando.model.Cita;
import com.practicando.practicando.model.EstadoCita;
import com.practicando.practicando.model.MetodoPago;
import com.practicando.practicando.service.CitaService;
import com.practicando.practicando.service.PagoService;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final CitaService citaService;

    public PagoController(
            PagoService pagoService,
            CitaService citaService) {

        this.pagoService = pagoService;
        this.citaService = citaService;
    }

    @GetMapping
    public String listar(Model model) {

        // Historial de pagos registrados
        model.addAttribute(
                "pagos",
                pagoService.listarTodos());

        // Resumen financiero de las citas completadas
        model.addAttribute(
                "resumenesPago",
                pagoService.listarResumenesPago());

        return "pagos";
    }

    @GetMapping("/nuevo")
    public String nuevo(
            @RequestParam(required = false) Long citaId,
            Model model) {

        PagoDTO pagoDTO = new PagoDTO();

        if (citaId != null) {
            pagoDTO.setCitaId(citaId);
        }

        model.addAttribute("pago", pagoDTO);

        cargarOpciones(model);

        return "pagos-form";
    }

    @PostMapping
    public String guardar(
            @Valid @ModelAttribute("pago") PagoDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarOpciones(model);
            return "pagos-form";
        }

        try {
            pagoService.registrarPago(dto);

        } catch (PagoInvalidoException e) {

            model.addAttribute("errorGeneral", e.getMessage());
            cargarOpciones(model);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Pago registrado correctamente");
        }

        return "redirect:/pagos";
    }

    private void cargarOpciones(Model model) {

        List<Cita> citasCompletadas = citaService.listarTodas()
                .stream()
                .filter(c -> c.getEstado() == EstadoCita.COMPLETADA)
                .filter(c -> {
                    BigDecimal saldo = pagoService.calcularSaldoPendiente(
                            c.getId());

                    return saldo.compareTo(BigDecimal.ZERO) > 0;
                })
                .toList();

        model.addAttribute("citas", citasCompletadas);
        model.addAttribute("metodosPago", MetodoPago.values());
    }

}
