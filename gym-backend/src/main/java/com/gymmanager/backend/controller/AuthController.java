package com.gymmanager.backend.controller;

import com.gymmanager.backend.dto.LoginRequest;
import com.gymmanager.backend.dto.LoginResponse;
import com.gymmanager.backend.model.Usuario;
import com.gymmanager.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.gymmanager.backend.dto.RegisterRequest;

import java.util.Optional;

import com.gymmanager.backend.dto.UpdateUserRequest;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private com.gymmanager.backend.repository.ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // Try to find by username first, then by email
        Optional<Usuario> userOpt = usuarioRepository.findByUsername(loginRequest.getUsername());
        if (!userOpt.isPresent()) {
            userOpt = usuarioRepository.findByEmail(loginRequest.getUsername());
        }

        if (userOpt.isPresent()) {
            Usuario user = userOpt.get();
            String dbPassword = user.getPassword();

            // Migration logic: If password is not BCrypt (doesn't start with $2a$), check as plain text
            boolean passwordMatches = false;
            if (!dbPassword.startsWith("$2a$")) {
                if (loginRequest.getPassword().equals(dbPassword)) {
                    // Encrypt and save for future logins
                    user.setPassword(passwordEncoder.encode(dbPassword));
                    usuarioRepository.save(user);
                    passwordMatches = true;
                }
            } else {
                passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), dbPassword);
            }

            if (passwordMatches) {
                Long clienteId = null;
                if (!"ADMIN".equals(user.getRole())) {
                    // Try to find associated client
                    clienteId = clienteRepository.findByEmail(user.getEmail())
                        .map(com.gymmanager.backend.model.Cliente::getId)
                        .orElse(null);
                }
                return ResponseEntity.ok(new LoginResponse(true, "Login successful", user, clienteId));
            }
        }

        return ResponseEntity.status(401).body(new LoginResponse(false, "Credenciales incorrectas"));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest registerRequest) {
        if (usuarioRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.status(400).body(new LoginResponse(false, "El nombre de usuario ya está en uso"));
        }

        Usuario newUser = new Usuario();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setEmail(registerRequest.getEmail());
        newUser.setRole("CLIENTE");
        newUser.setCuotaMensual(29.99);

        usuarioRepository.save(newUser);

        // SYNC: Create a Cliente record so the Admin can see it
        com.gymmanager.backend.model.Cliente newCliente = new com.gymmanager.backend.model.Cliente();
        newCliente.setNombre(newUser.getUsername());
        newCliente.setApellidos("Registrado App");
        newCliente.setEmail(newUser.getEmail());
        newCliente.setDni("APP-" + newUser.getId()); // Placeholder if DNI is missing
        clienteRepository.save(newCliente);

        return ResponseEntity.ok(new LoginResponse(true, "Registration successful", newUser));
    }

    @PutMapping("/update")
    public ResponseEntity<LoginResponse> updateProfile(@RequestBody UpdateUserRequest updateRequest) {
        // Find user by username or email to ensure update works
        Optional<Usuario> userOpt = usuarioRepository.findByUsername(updateRequest.getUsername());
        if (!userOpt.isPresent()) {
            userOpt = usuarioRepository.findByEmail(updateRequest.getUsername());
        }

        if (userOpt.isPresent()) {
            Usuario user = userOpt.get();
            if (updateRequest.getEmail() != null) user.setEmail(updateRequest.getEmail());
            if (updateRequest.getTelefono() != null) user.setTelefono(updateRequest.getTelefono());
            if (updateRequest.getDni() != null) user.setDni(updateRequest.getDni());
            if (updateRequest.getAvatarUrl() != null) user.setAvatarUrl(updateRequest.getAvatarUrl());
            if (updateRequest.getRole() != null) user.setRole(updateRequest.getRole());
            
            if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            }

            usuarioRepository.save(user);

            // SYNC: Update or Create Cliente record
            String dniToSearch = user.getDni() != null ? user.getDni() : "APP-" + user.getId();
            java.util.Optional<com.gymmanager.backend.model.Cliente> clienteOpt = clienteRepository.findByDni(dniToSearch);
            
            com.gymmanager.backend.model.Cliente cliente = clienteOpt.orElse(new com.gymmanager.backend.model.Cliente());
            cliente.setNombre(user.getUsername());
            cliente.setApellidos("Actualizado App");
            cliente.setDni(dniToSearch);
            cliente.setEmail(user.getEmail());
            cliente.setTelefono(user.getTelefono());
            clienteRepository.save(cliente);

            // Return full updated user info
            return ResponseEntity.ok(new LoginResponse(true, "Perfil actualizado con éxito", user));
        }

        return ResponseEntity.status(404).body(new LoginResponse(false, "Usuario no encontrado"));
    }
}
