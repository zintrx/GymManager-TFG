package com.gymmanager.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "actividades")
public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String descripcion;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    private String sala;

    private String instructor;
    
    @Column(name = "cupo_maximo")
    private Integer cupoMaximo = 20;

    @Column(name = "cupo_actual")
    private Integer cupoActual = 0;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public Integer getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(Integer cupoMaximo) { this.cupoMaximo = cupoMaximo; }
    public Integer getCupoActual() { return cupoActual; }
    public void setCupoActual(Integer cupoActual) { this.cupoActual = cupoActual; }
}
