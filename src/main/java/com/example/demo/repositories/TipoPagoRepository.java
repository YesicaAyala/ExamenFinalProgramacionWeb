package com.example.demo.repositories;

import com.example.demo.entities.TipoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoPagoRepository extends JpaRepository<TipoPago, Long> {
    Optional<TipoPago> findByNombre(String nombre);
}
