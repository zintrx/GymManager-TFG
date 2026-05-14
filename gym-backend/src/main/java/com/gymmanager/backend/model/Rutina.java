package com.gymmanager.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "rutinas")
public class Rutina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = true)
    private Cliente cliente;

    @Column(name = "nombre_rutina", nullable = false)
    private String nombreRutina;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_asignacion")
    private LocalDate fechaAsignacion = LocalDate.now();

    @Column(name = "dias_semana")
    private Integer diasSemana;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public String getNombreRutina() { return nombreRutina; }
    public void setNombreRutina(String nombreRutina) { this.nombreRutina = nombreRutina; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public Integer getDiasSemana() { return diasSemana; }
    public void setDiasSemana(Integer diasSemana) { this.diasSemana = diasSemana; }
}
