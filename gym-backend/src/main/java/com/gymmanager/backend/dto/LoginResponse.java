package com.gymmanager.backend.dto;

import com.gymmanager.backend.model.Usuario;

public class LoginResponse {
    private Long id;
    private boolean success;
    private String message;
    private String username;
    private String role;
    private String email;
    private String telefono;
    private String dni;
    private String avatarUrl;
    private Double cuotaMensual;
    private Long clienteId;

    public LoginResponse() {}

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResponse(boolean success, String message, Usuario user) {
        this(success, message, user, null);
    }

    public LoginResponse(boolean success, String message, Usuario user, Long clienteId) {
        this.success = success;
        this.message = message;
        this.clienteId = clienteId;
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.role = user.getRole();
            this.email = user.getEmail();
            this.telefono = user.getTelefono();
            this.dni = user.getDni();
            this.avatarUrl = user.getAvatarUrl();
            this.cuotaMensual = user.getCuotaMensual();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Double getCuotaMensual() { return cuotaMensual; }
    public void setCuotaMensual(Double cuotaMensual) { this.cuotaMensual = cuotaMensual; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
}
