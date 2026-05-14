package com.gymmanager.backend.controller;

import com.gymmanager.backend.model.Ejercicio;
import com.gymmanager.backend.repository.EjercicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ejercicios")
@CrossOrigin(origins = "*")
public class EjercicioController {

    @Autowired
    private EjercicioRepository ejercicioRepository;

    @GetMapping("/rutina/{rutinaId}")
    public List<Ejercicio> getEjerciciosByRutina(@PathVariable Long rutinaId) {
        return ejercicioRepository.findByRutinaId(rutinaId);
    }

    @PostMapping
    public Ejercicio createEjercicio(@RequestBody Ejercicio ejercicio) {
        return ejercicioRepository.save(ejercicio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ejercicio> updateEjercicio(@PathVariable Long id, @RequestBody Ejercicio ejercicioDetails) {
        return ejercicioRepository.findById(id).map(ejercicio -> {
            ejercicio.setNombre(ejercicioDetails.getNombre());
            ejercicio.setSeries(ejercicioDetails.getSeries());
            ejercicio.setRepeticiones(ejercicioDetails.getRepeticiones());
            ejercicio.setPeso(ejercicioDetails.getPeso());
            return ResponseEntity.ok(ejercicioRepository.save(ejercicio));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEjercicio(@PathVariable Long id) {
        return ejercicioRepository.findById(id).map(ejercicio -> {
            ejercicioRepository.delete(ejercicio);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
