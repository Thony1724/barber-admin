
package com.practicando.practicando.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambiarPasswordDTO {

    @NotBlank(message = "Ingresa tu contraseña actual")
    private String passwordActual;

    @NotBlank(message = "Ingresa una nueva contraseña")
    @Size(min = 12, message = "Debe tener al menos 12 caracteres")
    private String passwordNueva;

    @NotBlank(message = "Confirma la nueva contraseña")
    private String confirmarPassword;
}
