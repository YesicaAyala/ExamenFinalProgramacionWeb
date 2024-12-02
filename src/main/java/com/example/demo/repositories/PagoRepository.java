package com.example.demo.repositories;

import com.example.demo.entities.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
	
	 @Query("SELECT SUM(p.valor) FROM Pago p WHERE p.compra.id = :compraId")
	    double sumTotalByCompra(Long compraId);
}
