
package com.practicando.practicando.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.practicando.practicando.dto.UsuarioDTO;
import com.practicando.practicando.model.Usuario;
import com.practicando.practicando.repository.UsuarioRepository;

import com.practicando.practicando.model.Rol;
import org.springframework.security.access.AccessDeniedException;

import com.practicando.practicando.dto.CambiarPasswordDTO;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AuditoriaService auditoriaService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public void crearUsuario(UsuarioDTO dto) {

        String username = dto.getUsername().trim();

        if (usuarioRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "El nombre de usuario ya está registrado");
        }

        Usuario usuario = new Usuario();

        usuario.setUsername(username);

        usuario.setPasswordHash(
                passwordEncoder.encode(dto.getPassword()));

        usuario.setRol(dto.getRol());
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        auditoriaService.registrar(
                "CREAR_USUARIO",
                "USUARIOS",
                "Se creó la cuenta: " + usuario.getUsername());

    }

    @Transactional
    public void desactivarUsuario(
            Long id,
            String usernameActual) {

        Usuario usuario = usuarioRepository
                .buscarPorIdConBloqueo(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El usuario no existe"));

        // No permitir desactivar la propia cuenta
        if (usuario.getUsername().equals(usernameActual)) {
            throw new IllegalArgumentException(
                    "No puedes desactivar tu propia cuenta");
        }

        // Si ya está inactivo, no hay nada que cambiar
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            return;
        }

        // Evitar dejar el sistema sin administradores activos
        if (usuario.getRol() == Rol.ADMIN
                && usuarioRepository.countByRolAndActivoTrue(
                        Rol.ADMIN) <= 1) {

            throw new IllegalArgumentException(
                    "No se puede desactivar al último administrador activo");
        }

        usuario.setActivo(false);

        usuarioRepository.save(usuario);

        auditoriaService.registrar(
                "DESACTIVAR_USUARIO",
                "USUARIOS",
                "Se desactivó la cuenta: " + usuario.getUsername());

    }

    @Transactional
    public void activarUsuario(Long id) {

        Usuario usuario = usuarioRepository
                .buscarPorIdConBloqueo(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El usuario no existe"));

        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        auditoriaService.registrar(
                "ACTIVAR_USUARIO",
                "USUARIOS",
                "Se activó la cuenta: " + usuario.getUsername());

    }

    @Transactional
    public void cambiarPassword(
            String username,
            CambiarPasswordDTO dto) {

        Usuario usuario = usuarioRepository
                .findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado"));

        if (!passwordEncoder.matches(
                dto.getPasswordActual(),
                usuario.getPasswordHash())) {

            throw new IllegalArgumentException(
                    "La contraseña actual es incorrecta");
        }

        if (!dto.getPasswordNueva().equals(
                dto.getConfirmarPassword())) {

            throw new IllegalArgumentException(
                    "Las nuevas contraseñas no coinciden");
        }

        if (passwordEncoder.matches(
                dto.getPasswordNueva(),
                usuario.getPasswordHash())) {

            throw new IllegalArgumentException(
                    "La nueva contraseña debe ser diferente a la actual");
        }

        usuario.setPasswordHash(
                passwordEncoder.encode(dto.getPasswordNueva()));

        usuarioRepository.save(usuario);

        auditoriaService.registrar(
                "CAMBIAR_PASSWORD",
                "SEGURIDAD",
                "El usuario actualizó su contraseña");

    }

    @Transactional
    public void restablecerPassword(
            Long usuarioId,
            String passwordNueva) {

        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado"));

        if (passwordNueva == null || passwordNueva.length() < 12) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 12 caracteres");
        }

        usuario.setPasswordHash(
                passwordEncoder.encode(passwordNueva));

        usuarioRepository.save(usuario);

        auditoriaService.registrar(
                "RESTABLECER_PASSWORD",
                "SEGURIDAD",
                "Se restableció la contraseña de: "
                        + usuario.getUsername());

    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado"));
    }

}
