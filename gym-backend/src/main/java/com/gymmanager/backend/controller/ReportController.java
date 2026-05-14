package com.gymmanager.backend.controller;

import com.gymmanager.backend.model.Cliente;
import com.gymmanager.backend.repository.ClienteRepository;
import com.gymmanager.backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private com.gymmanager.backend.repository.PagoRepository pagoRepository;

    @Autowired
    private com.gymmanager.backend.repository.ReservaRepository reservaRepository;

    @Autowired
    private com.gymmanager.backend.repository.ActividadRepository actividadRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/clientes")
    public ResponseEntity<byte[]> getClientesReport() {
        // ... (existing code)
        try {
            List<Cliente> clientes = clienteRepository.findAll();
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("Title", "Listado de Clientes GymManager");

            byte[] report = reportService.exportReport(clientes, parameters, "/reports/clientes.jrxml");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename("clientes_report.pdf").build());

            return new ResponseEntity<>(report, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. Revenue (Sum of all payments)
        double totalRevenue = pagoRepository.findAll().stream()
                .mapToDouble(p -> p.getMonto() != null ? p.getMonto().doubleValue() : 0.0)
                .sum();
        stats.put("totalRevenue", totalRevenue);

        // 2. Attendance per Activity
        Map<String, Long> attendance = new HashMap<>();
        actividadRepository.findAll().forEach(act -> {
            long count = reservaRepository.findByActividadId(act.getId()).size();
            attendance.put(act.getTitulo(), count);
        });
        stats.put("attendance", attendance);

        // 3. User distribution (Active vs Inactive)
        Map<String, Long> userStatus = new HashMap<>();
        long active = clienteRepository.findAll().stream().filter(c -> "ACTIVO".equals(c.getEstado())).count();
        long inactive = clienteRepository.findAll().stream().filter(c -> !"ACTIVO".equals(c.getEstado())).count();
        userStatus.put("ACTIVO", active);
        userStatus.put("INACTIVO", inactive);
        stats.put("userStatus", userStatus);

        return ResponseEntity.ok(stats);
    }
}
