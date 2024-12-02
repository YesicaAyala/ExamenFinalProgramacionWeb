package com.example.demo.repositories;

import com.example.demo.entities.DetallesCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleCompraRepository extends JpaRepository<DetallesCompra, Long> {
}
