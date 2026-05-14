package com.gymmanager.backend.service;

import com.gymmanager.backend.model.Publicacion;
import com.gymmanager.backend.repository.PublicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PublicacionService {

    @Autowired
    private PublicacionRepository publicacionRepository;

    public List<Publicacion> obtenerTodas() {
        return publicacionRepository.findAllByOrderByFechaDesc();
    }

    public Publicacion obtenerPorId(Long id) {
        return publicacionRepository.findById(id).orElse(null);
    }

    public Publicacion guardar(Publicacion publicacion) {
        return publicacionRepository.save(publicacion);
    }
}
