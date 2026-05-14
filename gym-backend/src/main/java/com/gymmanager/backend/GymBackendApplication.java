package com.gymmanager.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.gymmanager.backend.repository.UsuarioRepository;
import com.gymmanager.backend.model.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

@SpringBootApplication
public class GymBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymBackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner initAdmin(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Ensure admin user exists with hashed password "admin123"
			java.util.Optional<Usuario> adminOpt = usuarioRepository.findByUsername("admin");
			if (adminOpt.isPresent()) {
				Usuario admin = adminOpt.get();
				admin.setRole("ADMIN");
				admin.setPassword(passwordEncoder.encode("admin123"));
				usuarioRepository.save(admin);
				System.out.println("✅ Usuario 'admin' actualizado con contraseña 'admin123'");
			} else {
				Usuario newAdmin = new Usuario();
				newAdmin.setUsername("admin");
				newAdmin.setPassword(passwordEncoder.encode("admin123"));
				newAdmin.setEmail("admin@gymmanager.com");
				newAdmin.setRole("ADMIN");
				usuarioRepository.save(newAdmin);
				System.out.println("✅ Usuario 'admin' creado con contraseña 'admin123'");
			}

			// Original logic to fix other roles
			List<Usuario> usuarios = usuarioRepository.findAll();
			for (Usuario u : usuarios) {
				if ("ADMIN".equals(u.getRole()) && !"admin".equals(u.getUsername())) {
					u.setRole("CLIENTE");
					usuarioRepository.save(u);
					System.out.println("Rol corregido para el usuario: " + u.getUsername());
				}
			}
		};
	}
}
