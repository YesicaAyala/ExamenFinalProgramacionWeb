package com.example.demo.repositories;

import com.example.demo.entities.Cajero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CajeroRepository extends JpaRepository<Cajero, Long> {
    Optional<Cajero> findByToken(String token);
}
