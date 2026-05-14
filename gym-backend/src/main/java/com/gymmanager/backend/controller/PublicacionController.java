package com.gymmanager.backend.controller;

import com.gymmanager.backend.model.Publicacion;
import com.gymmanager.backend.model.Usuario;
import com.gymmanager.backend.service.PublicacionService;
import com.gymmanager.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publicaciones")
@CrossOrigin(origins = "*")
public class PublicacionController {

    @Autowired
    private PublicacionService publicacionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Publicacion> listar() {
        return publicacionService.obtenerTodas();
    }

    @PostMapping
    public ResponseEntity<Publicacion> crear(@RequestBody PublicacionRequest request) {
        // En un entorno real, el usuario vendría del contexto de seguridad
        // Para este TFG, buscaremos al usuario por ID o usaremos el primero si no se especifica
        Usuario autor = null;
        if (request.getUsuarioId() != null) {
            autor = usuarioRepository.findById(request.getUsuarioId()).orElse(null);
        }
        
        if (autor == null) {
            // Fallback: usar el primer usuario encontrado para la demo
            autor = usuarioRepository.findAll().stream().findFirst().orElse(null);
        }

        if (autor == null) {
            return ResponseEntity.badRequest().build();
        }

        Publicacion publicacion = new Publicacion();
        publicacion.setContenido(request.getContenido());
        publicacion.setAutor(autor);
        
        return ResponseEntity.ok(publicacionService.guardar(publicacion));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Publicacion> like(@PathVariable Long id) {
        Publicacion p = publicacionService.obtenerPorId(id);
        if (p == null) return ResponseEntity.notFound().build();
        
        p.setLikes(p.getLikes() + 1);
        return ResponseEntity.ok(publicacionService.guardar(p));
    }

    // Inner class for Request Body
    public static class PublicacionRequest {
        private String contenido;
        private Long usuarioId;

        public String getContenido() { return contenido; }
        public void setContenido(String contenido) { this.contenido = contenido; }

        public Long getUsuarioId() { return usuarioId; }
        public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    }
}
