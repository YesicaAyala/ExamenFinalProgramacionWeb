package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class FacturaRequest {
    private double impuesto; 
    private ClienteRequest cliente;               // Información del cliente
    private List<ProductoRequest> productos;      // Lista de productos
    private List<PagoRequest> medios_pago;         // Métodos de pago
    private VendedorRequest vendedor;             // Información del vendedor
    private CajeroRequest cajero;                 // Información del cajero                     // Impuesto aplicado a la factura
}
