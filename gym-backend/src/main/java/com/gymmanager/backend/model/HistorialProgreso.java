package com.gymmanager.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_progreso")
public class HistorialProgreso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "ejercicio_id", nullable = false)
    private Ejercicio ejercicio;

    private Integer seriesCompletadas;
    private Integer repeticionesRealizadas;
    private BigDecimal pesoUtilizado;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    private String notas;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Ejercicio getEjercicio() { return ejercicio; }
    public void setEjercicio(Ejercicio ejercicio) { this.ejercicio = ejercicio; }
    public Integer getSeriesCompletadas() { return seriesCompletadas; }
    public void setSeriesCompletadas(Integer seriesCompletadas) { this.seriesCompletadas = seriesCompletadas; }
    public Integer getRepeticionesRealizadas() { return repeticionesRealizadas; }
    public void setRepeticionesRealizadas(Integer repeticionesRealizadas) { this.repeticionesRealizadas = repeticionesRealizadas; }
    public BigDecimal getPesoUtilizado() { return pesoUtilizado; }
    public void setPesoUtilizado(BigDecimal pesoUtilizado) { this.pesoUtilizado = pesoUtilizado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
