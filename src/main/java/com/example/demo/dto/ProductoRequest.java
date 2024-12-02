package com.example.demo.dto;

import lombok.Data;

@Data
public class ProductoRequest {
    private String referencia;   // Referencia del producto, por ejemplo: "ELEC001"
    private int cantidad;        // Cantidad del producto
    private int descuento;       // Descuento en porcentaje, por ejemplo: 10 (para un 10% de descuento)
}
