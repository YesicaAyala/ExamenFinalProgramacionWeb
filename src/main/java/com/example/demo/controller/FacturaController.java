package com.example.demo.controller;

import com.example.demo.entities.Compra;
import com.example.demo.models.FacturaRequest;
import com.example.demo.services.FacturaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @PostMapping("/{tiendaId}")
    public ResponseEntity<Compra> procesarFactura(@RequestBody FacturaRequest facturaRequest,
                                                  @PathVariable Long tiendaId) {
        Compra compra = facturaService.procesarFactura(facturaRequest, tiendaId);
        return ResponseEntity.ok(compra);
    }
}
