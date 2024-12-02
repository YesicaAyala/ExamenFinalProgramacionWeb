package com.example.demo.dto;

import lombok.Data;

@Data
public class ClienteRequest {
    private String documento;  // Documento del cliente
    private String nombre;     // Nombre del cliente
    private String tipoDocumento;  // Tipo de documento (CC, NIT, etc.)
}
