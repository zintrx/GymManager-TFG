package com.gymmanager.backend.repository;

import com.gymmanager.backend.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByClienteId(Long clienteId);
}
