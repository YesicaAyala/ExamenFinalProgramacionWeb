package com.example.demo.repositories;

import com.example.demo.entities.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
}
