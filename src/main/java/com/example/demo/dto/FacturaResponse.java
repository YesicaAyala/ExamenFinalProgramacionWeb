package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponse {
    private String status;
    private String message;
    private FacturaData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class FacturaData {
        private String numero;
        private String total;
        private String fecha;
    }
}


