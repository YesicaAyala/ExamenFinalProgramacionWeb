package com.example.demo.controller;

import com.example.demo.dto.FacturaRequest;
import com.example.demo.dto.FacturaResponse;
import com.example.demo.services.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    @Autowired
    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    // Endpoint para procesar la factura
    @PostMapping("/{tiendaId}")
    public ResponseEntity<FacturaResponse> procesarFactura(
            @PathVariable Long tiendaId, 
            @RequestBody FacturaRequest facturaRequest) {
        
        // Llamada al servicio para procesar la factura
        FacturaResponse response = facturaService.procesarFactura(tiendaId, facturaRequest);
        
        // Retornar la respuesta formateada con un estado 200 (OK)
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
