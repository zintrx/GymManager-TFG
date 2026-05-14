package com.gymmanager.backend.controller;

import com.gymmanager.backend.model.Actividad;
import com.gymmanager.backend.model.Reserva;
import com.gymmanager.backend.model.Usuario;
import com.gymmanager.backend.repository.ActividadRepository;
import com.gymmanager.backend.repository.ReservaRepository;
import com.gymmanager.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/actividad/{actividadId}/usuario/{usuarioId}")
    public ResponseEntity<?> reservar(@PathVariable Long actividadId, @PathVariable Long usuarioId) {
        Optional<Actividad> actOpt = actividadRepository.findById(actividadId);
        Optional<Usuario> userOpt = usuarioRepository.findById(usuarioId);

        if (!actOpt.isPresent() || !userOpt.isPresent()) {
            return ResponseEntity.status(404).body("Actividad o Usuario no encontrado");
        }

        Actividad actividad = actOpt.get();
        Usuario usuario = userOpt.get();

        if (reservaRepository.existsByUsuarioIdAndActividadId(usuarioId, actividadId)) {
            return ResponseEntity.status(400).body("Ya tienes una reserva para esta actividad");
        }

        if (actividad.getCupoActual() >= actividad.getCupoMaximo()) {
            return ResponseEntity.status(400).body("La actividad está llena");
        }

        // Crear reserva
        Reserva reserva = new Reserva();
        reserva.setActividad(actividad);
        reserva.setUsuario(usuario);
        reservaRepository.save(reserva);

        // Actualizar cupo
        actividad.setCupoActual(actividad.getCupoActual() + 1);
        actividadRepository.save(actividad);

        return ResponseEntity.ok(reserva);
    }

    @DeleteMapping("/actividad/{actividadId}/usuario/{usuarioId}")
    public ResponseEntity<?> cancelar(@PathVariable Long actividadId, @PathVariable Long usuarioId) {
        List<Reserva> reservas = reservaRepository.findByActividadId(actividadId);
        Optional<Reserva> reservaOpt = reservas.stream()
                .filter(r -> r.getUsuario().getId().equals(usuarioId))
                .findFirst();

        if (!reservaOpt.isPresent()) {
            return ResponseEntity.status(404).body("Reserva no encontrada");
        }

        reservaRepository.delete(reservaOpt.get());

        // Actualizar cupo
        Optional<Actividad> actOpt = actividadRepository.findById(actividadId);
        if (actOpt.isPresent()) {
            Actividad actividad = actOpt.get();
            if (actividad.getCupoActual() > 0) {
                actividad.setCupoActual(actividad.getCupoActual() - 1);
                actividadRepository.save(actividad);
            }
        }

        return ResponseEntity.ok("Reserva cancelada");
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Reserva> getReservasUsuario(@PathVariable Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }
}
