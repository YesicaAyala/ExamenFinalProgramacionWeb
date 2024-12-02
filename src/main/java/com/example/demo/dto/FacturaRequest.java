package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class FacturaRequest {
    private ClienteRequest cliente;               // Información del cliente
    private List<ProductoRequest> productos;      // Lista de productos
    private List<PagoRequest> mediosPago;         // Métodos de pago
    private VendedorRequest vendedor;             // Información del vendedor
    private CajeroRequest cajero;                 // Información del cajero
    private double impuesto;                      // Impuesto aplicado a la factura
}
