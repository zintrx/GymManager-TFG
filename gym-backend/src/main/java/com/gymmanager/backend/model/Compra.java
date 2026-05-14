package com.gymmanager.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "compras")
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String producto;
    private Double precio;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @JsonProperty("usuario")
    public java.util.Map<String, String> getUsuarioSummary() {
        java.util.HashMap<String, String> summary = new java.util.HashMap<>();
        summary.put("username", usuario != null ? usuario.getUsername() : "Anónimo");
        return summary;
    }

    @Column(name = "fecha", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fecha = LocalDateTime.now();

    public Compra() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    @JsonProperty("usuarioId")
    public Long getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }
}
