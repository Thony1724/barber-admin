
package com.practicando.practicando.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.practicando.practicando.service.DashboardService;

@Controller
public class HomeController {

    private final DashboardService dashboardService;

    public HomeController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String inicio(Model model) {

        model.addAttribute("totalClientes",
                dashboardService.contarClientes());

        model.addAttribute("barberosActivos",
                dashboardService.contarBarberosActivos());

        model.addAttribute("serviciosActivos",
                dashboardService.contarServiciosActivos());

        model.addAttribute("citasHoy",
                dashboardService.contarCitasHoy());
        model.addAttribute("citasDelDia",
                dashboardService.obtenerCitasHoy());
        return "index";
    }
}
