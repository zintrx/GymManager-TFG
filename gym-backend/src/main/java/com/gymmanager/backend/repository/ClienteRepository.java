package com.gymmanager.backend.repository;

import com.gymmanager.backend.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
    java.util.Optional<Cliente> findByDni(String dni);
    java.util.Optional<Cliente> findByEmail(String email);
}
