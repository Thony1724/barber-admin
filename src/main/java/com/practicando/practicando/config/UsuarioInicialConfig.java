
package com.practicando.practicando.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.practicando.practicando.model.Rol;
import com.practicando.practicando.model.Usuario;
import com.practicando.practicando.repository.UsuarioRepository;

@Configuration
public class UsuarioInicialConfig {

    @Bean
    public CommandLineRunner crearAdministradorInicial(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.initial-password:}")
            String passwordInicial) {

        return args -> {

            if (usuarioRepository.count() > 0) {
                return;
            }

            if (passwordInicial == null
                    || passwordInicial.length() < 12) {

                throw new IllegalStateException(
                    "Para crear el primer administrador, "
                    + "configura APP_ADMIN_INITIAL_PASSWORD "
                    + "con al menos 12 caracteres."
                );
            }

            Usuario administrador = new Usuario();

            administrador.setUsername("admin");

            administrador.setPasswordHash(
                    passwordEncoder.encode(passwordInicial)
            );

            administrador.setRol(Rol.ADMIN);

            administrador.setActivo(true);

            usuarioRepository.save(administrador);

            System.out.println(
                "Administrador inicial creado correctamente."
            );
        };
    }
}
