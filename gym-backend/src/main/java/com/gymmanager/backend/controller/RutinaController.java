package com.gymmanager.backend.controller;

import com.gymmanager.backend.model.Cliente;
import com.gymmanager.backend.model.Rutina;
import com.gymmanager.backend.repository.ClienteRepository;
import com.gymmanager.backend.repository.EjercicioRepository;
import com.gymmanager.backend.repository.RutinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutinas")
@CrossOrigin(origins = "*")
public class RutinaController {

    @Autowired
    private RutinaRepository rutinaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EjercicioRepository ejercicioRepository;

    @GetMapping
    public List<Rutina> getAllRutinas() {
        return rutinaRepository.findAll();
    }

    @GetMapping("/plantillas")
    public List<Rutina> getPlantillas() {
        return rutinaRepository.findByClienteIsNull();
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Rutina> getRutinasByCliente(@PathVariable Long clienteId) {
        return rutinaRepository.findByClienteId(clienteId);
    }

    @GetMapping("/usuario/{username}")
    public List<Rutina> getRutinasByUsername(@PathVariable String username) {
        return rutinaRepository.findAll(); 
    }

    @PostMapping
    public Rutina createRutina(@RequestBody Rutina rutina) {
        // Si el cliente viene con ID null o el objeto cliente es null, es una plantilla
        if (rutina.getCliente() != null && rutina.getCliente().getId() == null) {
            rutina.setCliente(null);
        }
        return rutinaRepository.save(rutina);
    }

    @PostMapping("/{id}/asignar/{clienteId}")
    public ResponseEntity<Rutina> asignarPlantilla(@PathVariable Long id, @PathVariable Long clienteId) {
        return rutinaRepository.findById(id).map(plantilla -> {
            return clienteRepository.findById(clienteId).map(cliente -> {
                // Clonar la rutina
                Rutina nuevaRutina = new Rutina();
                nuevaRutina.setNombreRutina(plantilla.getNombreRutina());
                nuevaRutina.setDescripcion(plantilla.getDescripcion());
                nuevaRutina.setCliente(cliente);
                nuevaRutina.setDiasSemana(plantilla.getDiasSemana());
                Rutina guardada = rutinaRepository.save(nuevaRutina);

                // Clonar ejercicios
                List<com.gymmanager.backend.model.Ejercicio> ejercicios = ejercicioRepository.findByRutinaId(id);
                for (com.gymmanager.backend.model.Ejercicio ex : ejercicios) {
                    com.gymmanager.backend.model.Ejercicio nuevoEx = new com.gymmanager.backend.model.Ejercicio();
                    nuevoEx.setNombre(ex.getNombre());
                    nuevoEx.setSeries(ex.getSeries());
                    nuevoEx.setRepeticiones(ex.getRepeticiones());
                    nuevoEx.setPeso(ex.getPeso());
                    nuevoEx.setRutina(guardada);
                    ejercicioRepository.save(nuevoEx);
                }
                return ResponseEntity.ok(guardada);
            }).orElse(ResponseEntity.notFound().build());
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rutina> updateRutina(@PathVariable Long id, @RequestBody Rutina rutinaDetails) {
        return rutinaRepository.findById(id).map(rutina -> {
            rutina.setNombreRutina(rutinaDetails.getNombreRutina());
            rutina.setDescripcion(rutinaDetails.getDescripcion());
            rutina.setDiasSemana(rutinaDetails.getDiasSemana());
            return ResponseEntity.ok(rutinaRepository.save(rutina));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRutina(@PathVariable Long id) {
        return rutinaRepository.findById(id).map(rutina -> {
            rutinaRepository.delete(rutina);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
