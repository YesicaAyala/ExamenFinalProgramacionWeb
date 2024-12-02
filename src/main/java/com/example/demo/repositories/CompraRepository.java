package com.example.demo.repositories;

import com.example.demo.entities.Compra;
import com.example.demo.entities.Tienda;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraRepository extends JpaRepository<Compra, Long> {

	Optional<Compra> findById(Integer facturaId);
}
