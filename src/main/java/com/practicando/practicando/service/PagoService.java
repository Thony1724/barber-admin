package com.practicando.practicando.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.practicando.practicando.dto.PagoDTO;
import com.practicando.practicando.dto.ResumenPagoDTO;

import com.practicando.practicando.exception.PagoInvalidoException;

import com.practicando.practicando.model.Cita;
import com.practicando.practicando.model.EstadoCita;
import com.practicando.practicando.model.EstadoPago;
import com.practicando.practicando.model.Pago;

import com.practicando.practicando.repository.CitaRepository;
import com.practicando.practicando.repository.PagoRepository;

import com.practicando.practicando.dto.ReporteMetodoPagoDTO;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final CitaRepository citaRepository;
    private final AuditoriaService auditoriaService;

    public PagoService(
            PagoRepository pagoRepository,
            CitaRepository citaRepository,
            AuditoriaService auditoriaService) {

        this.pagoRepository = pagoRepository;
        this.citaRepository = citaRepository;
        this.auditoriaService = auditoriaService;
    }

    // 1. Listar todos los pagos
    @Transactional(readOnly = true)
    public List<Pago> listarTodos() {
        return pagoRepository.findAllByOrderByFechaPagoDesc();
    }

    // 2. Calcular el total pagado de una cita
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPagado(Long citaId) {
        return pagoRepository.calcularTotalPagadoPorCita(citaId);
    }

    // 3. Calcular el saldo pendiente
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoPendiente(Long citaId) {

        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new PagoInvalidoException(
                        "La cita no existe"));

        BigDecimal totalPagado = pagoRepository.calcularTotalPagadoPorCita(citaId);

        return cita.getPrecio().subtract(totalPagado);
    }

    // 4. Registrar un pago
    @Transactional
    public void registrarPago(PagoDTO dto) {

        if (dto.getCitaId() == null ||
                dto.getMonto() == null ||
                dto.getMetodoPago() == null) {

            throw new PagoInvalidoException(
                    "Complete todos los datos del pago");
        }

        if (dto.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagoInvalidoException(
                    "El monto debe ser mayor que cero");
        }

        // Bloquear la cita durante el registro del pago
        Cita cita = citaRepository
                .buscarPorIdConBloqueo(dto.getCitaId())
                .orElseThrow(() -> new PagoInvalidoException(
                        "La cita seleccionada no existe"));

        if (cita.getEstado() != EstadoCita.COMPLETADA) {
            throw new PagoInvalidoException(
                    "Solo se pueden registrar pagos de citas completadas");
        }

        BigDecimal totalPagado = pagoRepository.calcularTotalPagadoPorCita(cita.getId());

        BigDecimal saldoPendiente = cita.getPrecio().subtract(totalPagado);

        if (saldoPendiente.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagoInvalidoException(
                    "Esta cita ya está completamente pagada");
        }

        if (dto.getMonto().compareTo(saldoPendiente) > 0) {
            throw new PagoInvalidoException(
                    "El monto supera el saldo pendiente de S/ "
                            + saldoPendiente);
        }

        Pago pago = new Pago();

        pago.setCita(cita);
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setFechaPago(LocalDateTime.now());

        pagoRepository.save(pago);

        auditoriaService.registrar(
                "REGISTRAR_PAGO",
                "PAGOS",
                "Pago de S/ " + pago.getMonto()
                        + " para la cita #" + cita.getId()
                        + " mediante " + pago.getMetodoPago());
    }

    // 5. Calcular ingresos registrados en una fecha
    @Transactional(readOnly = true)
    public BigDecimal calcularIngresosPorFecha(LocalDate fecha) {

        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();

        return pagoRepository.calcularIngresosEntreFechas(inicio, fin);
    }

    // 6. Obtener el resumen financiero de una cita
    @Transactional(readOnly = true)
    public ResumenPagoDTO obtenerResumenPago(Long citaId) {

        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new PagoInvalidoException(
                        "La cita seleccionada no existe"));

        BigDecimal totalPagado = pagoRepository.calcularTotalPagadoPorCita(citaId);

        BigDecimal saldoPendiente = cita.getPrecio().subtract(totalPagado);

        EstadoPago estadoPago;

        if (totalPagado.compareTo(BigDecimal.ZERO) == 0) {

            estadoPago = EstadoPago.PENDIENTE;

        } else if (saldoPendiente.compareTo(BigDecimal.ZERO) > 0) {

            estadoPago = EstadoPago.PARCIAL;

        } else {

            estadoPago = EstadoPago.PAGADO;
        }

        return new ResumenPagoDTO(
                cita.getId(),
                cita.getCliente().getNombre() + " "
                        + cita.getCliente().getApellido(),
                cita.getServicio().getNombre(),
                cita.getPrecio(),
                totalPagado,
                saldoPendiente,
                estadoPago);
    }

    @Transactional(readOnly = true)
    public List<ResumenPagoDTO> listarResumenesPago() {

        return citaRepository.findAllByOrderByFechaAscHoraAsc()
                .stream()
                .filter(cita -> cita.getEstado() == EstadoCita.COMPLETADA)
                .map(cita -> obtenerResumenPago(cita.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularIngresosDelMes(
            int anio,
            int mes) {

        LocalDate inicio = LocalDate.of(anio, mes, 1);

        LocalDateTime desde = inicio.atStartOfDay();
        LocalDateTime hasta = inicio.plusMonths(1).atStartOfDay();

        return pagoRepository.calcularIngresosEntreFechas(
                desde,
                hasta);
    }

    @Transactional(readOnly = true)
    public List<ReporteMetodoPagoDTO> obtenerIngresosPorMetodo(
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        if (fechaInicio == null || fechaFin == null ||
                fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException(
                    "El rango de fechas no es válido");
        }

        LocalDateTime desde = fechaInicio.atStartOfDay();
        LocalDateTime hasta = fechaFin.plusDays(1).atStartOfDay();

        return pagoRepository.calcularIngresosPorMetodo(desde, hasta)
                .stream()
                .map(fila -> new ReporteMetodoPagoDTO(
                        (com.practicando.practicando.model.MetodoPago) fila[0],
                        (BigDecimal) fila[1]))
                .toList();
    }

}
