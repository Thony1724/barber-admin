package com.practicando.practicando.service;

import com.practicando.practicando.dto.ServicioDTO;
import com.practicando.practicando.model.Servicio;
import com.practicando.practicando.repository.ServicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    public List<Servicio> listarTodos() {
        return servicioRepository.findAll();
    }

    public List<Servicio> listarActivos() {
        return servicioRepository.findByActivoTrue();
    }

    public Servicio buscarPorId(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(
                    () -> new RuntimeException("Servicio no encontrado")
                );
    }

    public Servicio guardar(ServicioDTO servicioDTO) {

        Servicio servicio = new Servicio();

        servicio.setNombre(servicioDTO.getNombre());
        servicio.setDescripcion(servicioDTO.getDescripcion());
        servicio.setPrecio(servicioDTO.getPrecio());
        servicio.setDuracionMinutos(servicioDTO.getDuracionMinutos());

        return servicioRepository.save(servicio);
    }

    public Servicio actualizar(Long id, ServicioDTO servicioDTO) {

        Servicio servicio = buscarPorId(id);

        servicio.setNombre(servicioDTO.getNombre());
        servicio.setDescripcion(servicioDTO.getDescripcion());
        servicio.setPrecio(servicioDTO.getPrecio());
        servicio.setDuracionMinutos(servicioDTO.getDuracionMinutos());

        return servicioRepository.save(servicio);
    }

    public void desactivar(Long id) {

        Servicio servicio = buscarPorId(id);

        servicio.setActivo(false);

        servicioRepository.save(servicio);
    }

    public void activar(Long id) {

        Servicio servicio = buscarPorId(id);

        servicio.setActivo(true);

        servicioRepository.save(servicio);
    }

}