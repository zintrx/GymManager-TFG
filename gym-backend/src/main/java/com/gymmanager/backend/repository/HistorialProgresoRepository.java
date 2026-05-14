package com.gymmanager.backend.repository;

import com.gymmanager.backend.model.HistorialProgreso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialProgresoRepository extends JpaRepository<HistorialProgreso, Long> {
    List<HistorialProgreso> findByUsuarioId(Long usuarioId);
    List<HistorialProgreso> findByUsuarioIdAndEjercicioId(Long usuarioId, Long ejercicioId);
}
