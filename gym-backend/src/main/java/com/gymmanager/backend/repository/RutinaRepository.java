package com.gymmanager.backend.repository;

import com.gymmanager.backend.model.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RutinaRepository extends JpaRepository<Rutina, Long> {
    List<Rutina> findByClienteId(Long clienteId);
    List<Rutina> findByClienteIsNull();
}
