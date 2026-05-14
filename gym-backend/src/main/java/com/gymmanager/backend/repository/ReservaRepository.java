package com.gymmanager.backend.repository;

import com.gymmanager.backend.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioId(Long usuarioId);
    List<Reserva> findByActividadId(Long actividadId);
    boolean existsByUsuarioIdAndActividadId(Long usuarioId, Long actividadId);
}
