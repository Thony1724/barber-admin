
package com.practicando.practicando.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.practicando.practicando.model.Rol;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDTO {

    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 4, max = 50,
          message = "Debe tener entre 4 y 50 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 12,
          message = "La contraseña debe tener al menos 12 caracteres")
    private String password;

    @NotNull(message = "Seleccione un rol")
    private Rol rol;
}
