package com.pollerianorkys.restaurant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;
    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;
    @NotBlank(message = "El correo es oblligatorio")
    @Email(message = "El correo debe ser valido")
    private String email;
    @NotBlank(message = "El numero de telefono es oblligatorio")
    private String phone;
    @NotBlank(message = "El nombre de usuario es oblligatorio")
    private String username;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
