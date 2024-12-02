package com.example.demo.services;

import com.example.demo.entities.Compra;
import com.example.demo.models.FacturaRequest;

public interface FacturaService {
    Compra procesarFactura(FacturaRequest facturaRequest, Long tiendaId);
}
