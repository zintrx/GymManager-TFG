package com.gymmanager.backend.repository;

import com.gymmanager.backend.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {
    List<Ejercicio> findByRutinaId(Long rutinaId);
}
