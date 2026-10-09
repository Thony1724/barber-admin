
package com.practicando.practicando.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // 1. Rutas públicas
                        .requestMatchers(
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**")
                        .permitAll()

                        // 2. Rutas exclusivas para ADMIN

                        .requestMatchers(
                                "/usuarios",
                                "/usuarios/**",
                                "/reportes",
                                "/reportes/**",
                                "/auditoria",
                                "/auditoria/**")
                        .hasRole("ADMIN")

                        // 3. Resto del sistema: usuarios autenticados
                        .anyRequest().authenticated())

                // 4. Configuración del login
                .formLogin(form -> form

                        .loginPage("/login")

                        .defaultSuccessUrl("/", true)

                        .permitAll())

                // 5. Configuración del cierre de sesión
                .logout(logout -> logout

                        .logoutUrl("/logout")

                        .logoutSuccessUrl("/login?logout")

                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
