package com.gymmanager.backend.controller;

import com.gymmanager.backend.model.Cliente;
import com.gymmanager.backend.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*") // For development convenience
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Cliente createCliente(@RequestBody Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @RequestBody Cliente clienteDetails) {
        return clienteRepository.findById(id).map(cliente -> {
            cliente.setNombre(clienteDetails.getNombre());
            cliente.setApellidos(clienteDetails.getApellidos());
            cliente.setDni(clienteDetails.getDni());
            cliente.setTelefono(clienteDetails.getTelefono());
            cliente.setEmail(clienteDetails.getEmail());
            cliente.setEstado(clienteDetails.getEstado());
            cliente.setObservaciones(clienteDetails.getObservaciones());
            return ResponseEntity.ok(clienteRepository.save(cliente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        return clienteRepository.findById(id).map(cliente -> {
            clienteRepository.delete(cliente);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public List<Cliente> buscarClientes(@RequestParam String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }
}
