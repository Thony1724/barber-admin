package com.practicando.practicando.service;

import com.practicando.practicando.dto.BarberoDTO;
import com.practicando.practicando.exception.EmailDuplicadoException;
import com.practicando.practicando.model.Barbero;
import com.practicando.practicando.repository.BarberoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarberoService {

    private final BarberoRepository barberoRepository;

    public BarberoService(BarberoRepository barberoRepository) {
        this.barberoRepository = barberoRepository;
    }

    public List<Barbero> listarTodos() {
        return barberoRepository.findAll();
    }

    public Barbero buscarPorId(Long id) {
        return barberoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Barbero no encontrado"));
    }

    public Barbero guardar(BarberoDTO barberoDTO) {

        if (barberoRepository.existsByEmail(barberoDTO.getEmail())) {
            throw new EmailDuplicadoException(
                    "El correo electrónico ya está registrado");
        }

        Barbero barbero = new Barbero();

        barbero.setNombre(barberoDTO.getNombre());
        barbero.setApellido(barberoDTO.getApellido());
        barbero.setTelefono(barberoDTO.getTelefono());
        barbero.setEmail(barberoDTO.getEmail());
        barbero.setEspecialidad(barberoDTO.getEspecialidad());

        return barberoRepository.save(barbero);
    }

    public Barbero actualizar(Long id, BarberoDTO barberoDTO) {

        Barbero barbero = buscarPorId(id);

        if (barberoRepository.existsByEmailAndIdNot(
                barberoDTO.getEmail(), id)) {

            throw new EmailDuplicadoException(
                    "El correo electrónico ya está registrado");
        }

        barbero.setNombre(barberoDTO.getNombre());
        barbero.setApellido(barberoDTO.getApellido());
        barbero.setTelefono(barberoDTO.getTelefono());
        barbero.setEmail(barberoDTO.getEmail());
        barbero.setEspecialidad(barberoDTO.getEspecialidad());

        return barberoRepository.save(barbero);
    }

    public void desactivar(Long id) {

        Barbero barbero = buscarPorId(id);

        barbero.setActivo(false);

        barberoRepository.save(barbero);
    }

    public void activar(Long id) {

        Barbero barbero = buscarPorId(id);

        barbero.setActivo(true);

        barberoRepository.save(barbero);
    }

    public List<Barbero> listarActivos() {
        return barberoRepository.findByActivoTrue();
    }

}
