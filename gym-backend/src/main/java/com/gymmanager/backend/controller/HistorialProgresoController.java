package com.gymmanager.backend.controller;

import com.gymmanager.backend.model.HistorialProgreso;
import com.gymmanager.backend.repository.HistorialProgresoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progreso")
@CrossOrigin(origins = "*")
public class HistorialProgresoController {

    @Autowired
    private HistorialProgresoRepository progressRepository;

    @PostMapping
    public HistorialProgreso guardarProgreso(@RequestBody HistorialProgreso progreso) {
        return progressRepository.save(progreso);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<HistorialProgreso> getProgresoUsuario(@PathVariable Long usuarioId) {
        return progressRepository.findByUsuarioId(usuarioId);
    }

    @GetMapping("/usuario/{usuarioId}/ejercicio/{ejercicioId}")
    public List<HistorialProgreso> getProgresoEjercicio(@PathVariable Long usuarioId, @PathVariable Long ejercicioId) {
        return progressRepository.findByUsuarioIdAndEjercicioId(usuarioId, ejercicioId);
    }
}
