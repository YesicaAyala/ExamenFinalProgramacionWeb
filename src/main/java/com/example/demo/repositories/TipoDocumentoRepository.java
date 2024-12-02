package com.example.demo.repositories;

import com.example.demo.entities.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {
	@Query("SELECT t FROM TipoDocumento t WHERE nombre = 'CC'")
	TipoDocumento findByNombre(String nombre);
}
