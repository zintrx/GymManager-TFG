package com.gymmanager.backend.controller;

import com.gymmanager.backend.model.Compra;
import com.gymmanager.backend.model.Usuario;
import com.gymmanager.backend.repository.CompraRepository;
import com.gymmanager.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*")
public class CompraController {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> realizarCompra(@RequestBody CompraRequest request) {
        try {
            Usuario usuario = null;
            if (request.getUsuarioId() != null) {
                usuario = usuarioRepository.findById(request.getUsuarioId()).orElse(null);
            }
            if (usuario == null) {
                usuario = usuarioRepository.findAll().stream().findFirst().orElse(null);
            }

            if (usuario == null) {
                return ResponseEntity.status(404).body("No se encontró un usuario para asignar la compra");
            }

            Compra compra = new Compra();
            compra.setProducto(request.getProducto());
            compra.setPrecio(request.getPrecio());
            compra.setUsuario(usuario);

            return ResponseEntity.ok(compraRepository.save(compra));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Compra> listarPorUsuario(@PathVariable Long usuarioId) {
        // En un entorno real usaríamos un método del repositorio, 
        // para el TFG filtramos aquí por simplicidad
        return compraRepository.findAll().stream()
                .filter(c -> c.getUsuario().getId().equals(usuarioId))
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<Compra> listarTodas() {
        return compraRepository.findAll().stream()
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .collect(Collectors.toList());
    }

    public static class CompraRequest {
        private String producto;
        private Double precio;
        private Long usuarioId;

        public String getProducto() { return producto; }
        public void setProducto(String producto) { this.producto = producto; }
        public Double getPrecio() { return precio; }
        public void setPrecio(Double precio) { this.precio = precio; }
        public Long getUsuarioId() { return usuarioId; }
        public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    }
}
