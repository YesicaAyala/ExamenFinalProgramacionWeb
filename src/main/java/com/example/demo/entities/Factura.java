package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "facturas")
@Getter
@Setter
@ToString
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tienda_id", referencedColumnName = "id")
    private Tienda tienda;  // Relación con Tienda

    @ManyToOne
    @JoinColumn(name = "cliente_id", referencedColumnName = "id")
    private Cliente cliente;  // Relación con Cliente

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos;  // Relación con Productos

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pago> pagos;  // Relación con Pagos

    @ManyToOne
    @JoinColumn(name = "vendedor_id", referencedColumnName = "id")
    private Vendedor vendedor;  // Relación con Vendedor

    @ManyToOne
    @JoinColumn(name = "cajero_id", referencedColumnName = "id")
    private Cajero cajero;  // Relación con Cajero

    @Column(name = "fecha")
    private String fecha;  // Fecha de la factura (puede ser de tipo LocalDate si prefieres usar fechas)

    @Column(name = "total")
    private Double total;  // Total de la factura, que se calculará

    @Column(name = "impuesto")
    private Double impuesto;  // El impuesto aplicado en la factura

    // Otros campos que desees agregar según la lógica del negocio

    public Factura() {
        // Constructor vacío, importante para JPA
    }

    // Métodos adicionales como calcular el total, si es necesario
    public void calcularTotal() {
        // Aquí puedes implementar el cálculo del total, agregando productos, impuestos, etc.
    }
}
