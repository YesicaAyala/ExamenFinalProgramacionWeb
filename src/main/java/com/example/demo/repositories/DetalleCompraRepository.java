package com.example.demo.repositories;

import com.example.demo.entities.DetallesCompra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleCompraRepository extends JpaRepository<DetallesCompra, Long> {

	List<DetallesCompra> findByCompra_id(Long id);
}
