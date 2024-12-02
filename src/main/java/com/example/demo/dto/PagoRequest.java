package com.example.demo.dto;

import lombok.Data;

@Data
public class PagoRequest {
    private String tipo_pago;     // Tipo de pago, por ejemplo: "TARJETA CREDITO", "TARJETA DEBITO", "BITCOIN"
    private String tipo_tarjeta;  // Tipo de tarjeta (si aplica), por ejemplo: "MASTERCARD", "VISA"
    private int cuotas;          // Número de cuotas (si aplica, en caso de ser una tarjeta de crédito)
    private double valor;        // Valor del pago
}
