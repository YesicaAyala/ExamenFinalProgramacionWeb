package com.example.demo.repositories;

import com.example.demo.entities.Tienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TiendaRepository extends JpaRepository<Tienda, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario
}
