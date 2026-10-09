package com.practicando.practicando.service;

import com.practicando.practicando.dto.ClienteDTO;
import com.practicando.practicando.model.Cliente;
import com.practicando.practicando.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import com.practicando.practicando.exception.EmailDuplicadoException;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente guardar(ClienteDTO clienteDTO) {

        if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new EmailDuplicadoException(
                    "El correo electrónico ya está registrado");
        }

        Cliente cliente = new Cliente();

        cliente.setNombre(clienteDTO.getNombre());
        cliente.setApellido(clienteDTO.getApellido());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setEmail(clienteDTO.getEmail());

        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    public Cliente actualizar(Long id, ClienteDTO clienteDTO) {

        Cliente cliente = buscarPorId(id);

        if (clienteRepository.existsByEmailAndIdNot(
                clienteDTO.getEmail(),
                id)) {

            throw new EmailDuplicadoException(
                    "El correo electrónico ya está registrado");
        }

        cliente.setNombre(clienteDTO.getNombre());
        cliente.setApellido(clienteDTO.getApellido());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setEmail(clienteDTO.getEmail());

        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {

        Cliente cliente = buscarPorId(id);

        clienteRepository.delete(cliente);
    }
}