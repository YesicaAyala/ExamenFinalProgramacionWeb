package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponse {
    private String status;
    private String message;
    private FacturaData data;

    @Data
    public static class FacturaData {
        private String numero;
        private String total;
        private String fecha;
        private ClienteResponse cliente;
        private List<ProductoResponse> productos;
        private CajeroResponse cajero;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ClienteResponse {
            private String documento;
            private String nombre;
            private String tipoDocumento;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ProductoResponse {
            private String referencia;
            private String nombre;
            private int cantidad;
            private double precio;
            private double descuento;
            private double subtotal;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CajeroResponse {
            private String documento;
            private String nombre;
        }
    }
}
