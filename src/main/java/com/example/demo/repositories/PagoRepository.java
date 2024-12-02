package com.example.demo.repositories;

import com.example.demo.entities.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    // Método para encontrar un tipo de pago por su nombre
    @Query("SELECT t FROM TipoPago t WHERE t.nombre = :nombre")
    Optional<TipoPago> findTipoPagoByNombre(@Param("nombre") String nombre);
}
