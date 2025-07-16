package com.pollerianorkys.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Table(name = "menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del plato es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotNull(message = "El precio del plato es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    private Double precio;

    @NotBlank(message = "La descripcion del plato es obligatorio")
    @Column(nullable = false)
    private String descripcion;

    @NotBlank(message = "La URL de la imagen del platillo es obligatorio")
    @Column(nullable = false)
    private String urlImagen;

    @NotBlank(message = "La categoria del platillo es obligatorio")
    @Column(nullable = false)
    private String categoria;
}