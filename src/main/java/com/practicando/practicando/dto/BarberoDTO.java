package com.practicando.practicando.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BarberoDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    private String apellido;

    @Pattern(
        regexp = "^9\\d{8}$",
        message = "El celular debe empezar con 9 y contener 9 dígitos"
    )
    private String telefono;

    @Email(message = "Ingrese un correo electrónico válido")
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
    private String email;

    @Size(max = 150, message = "La especialidad no puede superar los 150 caracteres")
    private String especialidad;
}