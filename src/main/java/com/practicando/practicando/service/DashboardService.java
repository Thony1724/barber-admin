
package com.practicando.practicando.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.practicando.practicando.dto.CitaAgendaDTO;
import com.practicando.practicando.model.EstadoCita;

import com.practicando.practicando.repository.ClienteRepository;
import com.practicando.practicando.repository.BarberoRepository;
import com.practicando.practicando.repository.ServicioRepository;
import com.practicando.practicando.repository.CitaRepository;

@Service
public class DashboardService {

    private final ClienteRepository clienteRepository;
    private final BarberoRepository barberoRepository;
    private final ServicioRepository servicioRepository;
    private final CitaRepository citaRepository;
    private final CitaService citaService;
    private final PagoService pagoService;

    // Constructor: inyección de dependencias
    public DashboardService(
            ClienteRepository clienteRepository,
            BarberoRepository barberoRepository,
            ServicioRepository servicioRepository,
            CitaRepository citaRepository,
            CitaService citaService,
            PagoService pagoService) {

        this.clienteRepository = clienteRepository;
        this.barberoRepository = barberoRepository;
        this.servicioRepository = servicioRepository;
        this.citaRepository = citaRepository;
        this.citaService = citaService;
        this.pagoService = pagoService;
    }

    // 1. Contar clientes registrados
    @Transactional(readOnly = true)
    public long contarClientes() {
        return clienteRepository.count();
    }

    // 2. Contar barberos activos
    @Transactional(readOnly = true)
    public long contarBarberosActivos() {
        return barberoRepository.countByActivoTrue();
    }

    // 3. Contar servicios activos
    @Transactional(readOnly = true)
    public long contarServiciosActivos() {
        return servicioRepository.countByActivoTrue();
    }

    // 4. Contar citas de hoy, excluyendo canceladas
    @Transactional(readOnly = true)
    public long contarCitasHoy() {
        return citaRepository.countByFechaAndEstadoNot(
                LocalDate.now(),
                EstadoCita.CANCELADA
        );
    }

    // 5. Obtener las citas del día para mostrarlas en el Dashboard
    @Transactional(readOnly = true)
    public List<CitaAgendaDTO> obtenerCitasHoy() {
        return citaService.obtenerAgenda(LocalDate.now());
    }

    // 6. Calcular los ingresos reales del día
    @Transactional(readOnly = true)
    public BigDecimal calcularIngresosHoy() {
        return pagoService.calcularIngresosPorFecha(
                LocalDate.now()
        );
    }

}
