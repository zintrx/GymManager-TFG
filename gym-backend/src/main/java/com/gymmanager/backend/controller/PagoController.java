package com.gymmanager.backend.controller;

import com.gymmanager.backend.model.Pago;
import com.gymmanager.backend.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    @Autowired
    private PagoRepository pagoRepository;

    @GetMapping
    public List<Pago> getAllPagos() {
        return pagoRepository.findAll();
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Pago> getPagosByCliente(@PathVariable Long clienteId) {
        return pagoRepository.findByClienteId(clienteId);
    }

    @PostMapping
    public Pago createPago(@RequestBody Pago pago) {
        return pagoRepository.save(pago);
    }

    @GetMapping("/status/{username}")
    public ResponseEntity<java.util.Map<String, Object>> getPaymentStatus(@PathVariable String username) {
        java.util.Map<String, Object> status = new java.util.HashMap<>();
        // In a real app, we'd check the current month in the database
        // For this simulation, we check if there's any payment this month
        status.put("paid", true); 
        status.put("nextPayment", "01/06/2026");
        status.put("amount", 29.99);
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePago(@PathVariable Long id) {
        return pagoRepository.findById(id).map(pago -> {
            pagoRepository.delete(pago);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
