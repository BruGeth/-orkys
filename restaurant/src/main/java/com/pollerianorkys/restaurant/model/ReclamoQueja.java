package com.pollerianorkys.restaurant.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "reclamos_quejas")
@Data
public class ReclamoQueja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String restaurante;
    private String nombreCompleto;
    private String departamento;
    private String direccion;
    private String documento;
    private String telefono;
    private String email;
    private String celular;
    private String tipo; // producto o servicio
    private String numeroPedido;
    private LocalDate fechaPedido;
    private String canalPedido;
    private String montoReclamado;
    private String tipoReclamo; // reclamo o queja
    private String detalleReclamo;
    private LocalDate fechaObservacion;
    private String observaciones;
    private boolean consentimiento;

    public ReclamoQueja() {
    }
    
    

    public ReclamoQueja(Long id, String restaurante, String nombreCompleto, String departamento, String direccion, String documento, String telefono, String email, String celular, String tipo, String numeroPedido, LocalDate fechaPedido, String canalPedido, String montoReclamado, String tipoReclamo, String detalleReclamo, LocalDate fechaObservacion, String observaciones, boolean consentimiento) {
        this.id = id;
        this.restaurante = restaurante;
        this.nombreCompleto = nombreCompleto;
        this.departamento = departamento;
        this.direccion = direccion;
        this.documento = documento;
        this.telefono = telefono;
        this.email = email;
        this.celular = celular;
        this.tipo = tipo;
        this.numeroPedido = numeroPedido;
        this.fechaPedido = fechaPedido;
        this.canalPedido = canalPedido;
        this.montoReclamado = montoReclamado;
        this.tipoReclamo = tipoReclamo;
        this.detalleReclamo = detalleReclamo;
        this.fechaObservacion = fechaObservacion;
        this.observaciones = observaciones;
        this.consentimiento = consentimiento;
    }
    
    
    
    
}
