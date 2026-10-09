package com.practicando.practicando.service;

import com.practicando.practicando.dto.CitaDTO;
import com.practicando.practicando.exception.CitaInvalidaException;
import com.practicando.practicando.model.*;
import com.practicando.practicando.repository.*;
import com.practicando.practicando.dto.CitaAgendaDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final ClienteRepository clienteRepository;
    private final BarberoRepository barberoRepository;
    private final ServicioRepository servicioRepository;

    public CitaService(
            CitaRepository citaRepository,
            ClienteRepository clienteRepository,
            BarberoRepository barberoRepository,
            ServicioRepository servicioRepository) {

        this.citaRepository = citaRepository;
        this.clienteRepository = clienteRepository;
        this.barberoRepository = barberoRepository;
        this.servicioRepository = servicioRepository;
    }

    public List<Cita> listarTodas() {
        return citaRepository.findAllByOrderByFechaAscHoraAsc();
    }

    @Transactional
    public Cita guardar(CitaDTO dto) {

        // 1. Buscar el cliente
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new CitaInvalidaException("El cliente no existe"));

        // 2. Buscar el barbero
        Barbero barbero = barberoRepository.findById(dto.getBarberoId())
                .orElseThrow(() -> new CitaInvalidaException("El barbero no existe"));

        // 3. Buscar el servicio
        Servicio servicio = servicioRepository.findById(dto.getServicioId())
                .orElseThrow(() -> new CitaInvalidaException("El servicio no existe"));

        // 4. Validar disponibilidad
        if (!Boolean.TRUE.equals(barbero.getActivo())) {
            throw new CitaInvalidaException(
                    "El barbero seleccionado está inactivo");
        }

        if (!Boolean.TRUE.equals(servicio.getActivo())) {
            throw new CitaInvalidaException(
                    "El servicio seleccionado está inactivo");
        }

        // 5. Calcular el horario solicitado
        LocalDateTime inicio = LocalDateTime.of(
                dto.getFecha(),
                dto.getHora());

        LocalDateTime fin = inicio.plusMinutes(
                servicio.getDuracionMinutos());

        if (inicio.isBefore(LocalDateTime.now())) {
            throw new CitaInvalidaException(
                    "No puedes registrar una cita en el pasado");
        }

        // Por ahora, las citas deben terminar el mismo día
        if (!fin.toLocalDate().equals(dto.getFecha())) {
            throw new CitaInvalidaException(
                    "La cita debe finalizar el mismo día");
        }

        // 6. Consultar horarios ocupados
        List<Cita> citasExistentes = citaRepository.findByBarberoIdAndFechaAndEstadoNot(
                barbero.getId(),
                dto.getFecha(),
                EstadoCita.CANCELADA);

        // 7. Comprobar cruces de horarios
        for (Cita existente : citasExistentes) {

            LocalDateTime inicioExistente = LocalDateTime.of(
                    existente.getFecha(),
                    existente.getHora());

            LocalDateTime finExistente = inicioExistente.plusMinutes(
                    existente.getServicio().getDuracionMinutos());

            boolean hayCruce = inicio.isBefore(finExistente)
                    && fin.isAfter(inicioExistente);

            if (hayCruce) {
                throw new CitaInvalidaException(
                        "El barbero ya tiene una cita en ese horario");
            }
        }

        // 8. Crear la cita
        Cita cita = new Cita();

        cita.setCliente(cliente);
        cita.setBarbero(barbero);
        cita.setServicio(servicio);
        cita.setFecha(dto.getFecha());
        cita.setHora(dto.getHora());
        cita.setDuracionMinutos(servicio.getDuracionMinutos());
        cita.setPrecio(servicio.getPrecio());
        cita.setEstado(EstadoCita.PROGRAMADA);

        // 9. Guardar en MySQL
        return citaRepository.save(cita);
    }

    @Transactional
    public void confirmar(Long id) {
        Cita cita = buscarPorId(id);

        if (cita.getEstado() != EstadoCita.PROGRAMADA) {
            throw new CitaInvalidaException(
                    "Solo se pueden confirmar citas programadas");
        }

        cita.setEstado(EstadoCita.CONFIRMADA);
    }

    @Transactional
    public void completar(Long id) {
        Cita cita = buscarPorId(id);

        if (cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new CitaInvalidaException(
                    "Solo se pueden completar citas confirmadas");
        }

        cita.setEstado(EstadoCita.COMPLETADA);
    }

    @Transactional
    public void cancelar(Long id) {
        Cita cita = buscarPorId(id);

        if (cita.getEstado() != EstadoCita.PROGRAMADA
                && cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new CitaInvalidaException(
                    "Esta cita ya no se puede cancelar");
        }

        cita.setEstado(EstadoCita.CANCELADA);
    }

    @Transactional(readOnly = true)
    public Cita buscarPorId(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new CitaInvalidaException("La cita no existe"));
    }

    @Transactional(readOnly = true)
    public List<CitaAgendaDTO> obtenerAgenda(LocalDate fecha) {

        List<Cita> citas = citaRepository.findByFechaOrderByHoraAsc(fecha);

        return citas.stream()
        .map(cita -> new CitaAgendaDTO(
                cita.getId(),

                cita.getCliente().getNombre() + " " +
                cita.getCliente().getApellido(),

                cita.getBarbero().getId(),

                cita.getBarbero().getNombre() + " " +
                cita.getBarbero().getApellido(),

                cita.getServicio().getNombre(),

                cita.getHora(),

                cita.getHora().plusMinutes(
                        cita.getDuracionMinutos()
                ),

                cita.getEstado()
        ))
        .toList();
    }
}