package com.gymmanager.backend.dto;

public class UpdateUserRequest {
    private String username;
    private String email;
    private String telefono;
    private String dni;
    private String password;
    private String avatarUrl;
    private String role;

    public UpdateUserRequest() {}

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
