package com.pollerianorkys.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 *
 * @author Brunoo
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del usuario
    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    // Apellido del usuario
    @Column(nullable = false)
    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    // Correo electrónico
    @Column(nullable = false, unique = true)
    @NotBlank(message = "El correo es oblligatorio")
    @Email(message = "El correo debe ser valido")
    private String email;

    // Teléfono de contacto
    @Column(nullable = false, unique = true)
    @NotBlank(message = "El numero de telefono es oblligatorio")
    private String phone;

    // Nombre de usuario (único)
    @Column(nullable = false, unique = true)
    @NotBlank(message = "El nombre de usuario es oblligatorio")
    private String username;

    // Contraseña hasheada
    @Column(nullable = false)
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
