package com.example.demo.repositories;

import com.example.demo.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Producto findByReferencia(String referencia);
}
