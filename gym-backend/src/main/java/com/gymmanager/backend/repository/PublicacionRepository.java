package com.gymmanager.backend.repository;

import com.gymmanager.backend.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    List<Publicacion> findAllByOrderByFechaDesc();
}
