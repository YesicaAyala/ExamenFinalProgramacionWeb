package com.example.demo.controller;

import com.example.demo.dto.FacturaConsultRequest;
import com.example.demo.dto.FacturaRequest;
import com.example.demo.dto.FacturaResponse;
import com.example.demo.exception.ErrorResponse;
import com.example.demo.exception.NotFoundException;
import com.example.demo.services.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @PostMapping("/crear/{tiendaId}")
    public ResponseEntity<?> crearFactura(@PathVariable String tiendaId, @RequestBody FacturaRequest facturaRequest) {
        try {
            FacturaResponse facturaResponse = facturaService.procesarFactura(tiendaId, facturaRequest);
            return ResponseEntity.status(HttpStatus.OK).body(facturaResponse);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("error", e.getMessage(), null));
        }
    }
    // Endpoint for querying an invoice
    @PostMapping("/consultar/{tiendaUuid}")
    public ResponseEntity<?> consultarFactura(@PathVariable String tiendaUuid, @RequestBody FacturaConsultRequest facturaConsultRequest) {
        try {
            FacturaResponse facturaResponse = facturaService.consultarFactura(tiendaUuid, facturaConsultRequest.getToken(), facturaConsultRequest.getCliente(), facturaConsultRequest.getFactura());
            return ResponseEntity.status(HttpStatus.OK).body(facturaResponse);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("error", e.getMessage(), null));
        }
    }

    // DTO to handle the request for querying an invoice
    
}
