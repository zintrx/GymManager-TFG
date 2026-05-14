package com.gymmanager.backend.controller;

import com.gymmanager.backend.model.Actividad;
import com.gymmanager.backend.repository.ActividadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actividades")
@CrossOrigin(origins = "*")
public class ActividadController {

    @Autowired
    private ActividadRepository actividadRepository;

    @GetMapping
    public List<Actividad> getActividades() {
        return actividadRepository.findAll();
    }

    @PostMapping
    public Actividad saveActividad(@RequestBody Actividad actividad) {
        return actividadRepository.save(actividad);
    }

    @DeleteMapping("/{id}")
    public void deleteActividad(@PathVariable Long id) {
        actividadRepository.deleteById(id);
    }
}
