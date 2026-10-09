package com.practicando.practicando.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    private String apellido;

    @Pattern(regexp = "^\\d{9}$", message = "El teléfono debe contener exactamente 9 números")
    private String telefono;

    @Email(message = "Ingrese un correo electrónico válido")
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
    private String email;
}