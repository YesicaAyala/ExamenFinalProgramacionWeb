package com.example.demo.services;

import com.example.demo.dto.FacturaResponse;
import com.example.demo.dto.PagoRequest;
import com.example.demo.dto.ProductoRequest;
import com.example.demo.dto.FacturaData;
import com.example.demo.dto.FacturaRequest;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FacturaService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VendedorRepository vendedorRepository;

    @Autowired
    private CajeroRepository cajeroRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private DetalleCompraRepository detallesCompraRepository;

    @Transactional
    public FacturaResponse procesarFactura(Long tiendaId, FacturaRequest facturaRequest) {
        // Buscar cliente
        Cliente cliente = clienteRepository.findByDocumento(facturaRequest.getCliente().getDocumento())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Buscar vendedor
        Vendedor vendedor = vendedorRepository.findByDocumento(facturaRequest.getVendedor().getDocumento())
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

        // Buscar cajero
        Cajero cajero = cajeroRepository.findByToken(facturaRequest.getCajero().getToken())
                .orElseThrow(() -> new RuntimeException("Cajero no encontrado"));

        // Crear compra
        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setTiendaId(tiendaId);
        compra.setVendedor(vendedor);
        compra.setCajero(cajero);
        compra.setFecha(new java.sql.Timestamp(System.currentTimeMillis()));
        compraRepository.save(compra);

        BigDecimal total = BigDecimal.ZERO;

        // Procesar productos
        for (ProductoRequest productoRequest : facturaRequest.getProductos()) {
            Producto producto = productoRepository.findByReferencia(productoRequest.getReferencia())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            BigDecimal precioProducto = producto.getPrecio();
            Double descuento = Double.valueOf(productoRequest.getDescuento()).divide(BigDecimal.valueOf(100));
            Double precioFinal = precioProducto.multiply(Double.ONE.subtract(descuento));
            BigDecimal subTotal = precioFinal.multiply(BigDecimal.valueOf(productoRequest.getCantidad()));

            DetalleCompra detallesCompra = new DetalleCompra();
            detallesCompra.setCompra(compra);
            detallesCompra.setProducto(producto);
            detallesCompra.setCantidad(productoRequest.getCantidad());
            detallesCompra.setPrecio(precioFinal);
            detallesCompra.setDescuento(descuento);
            detallesCompraRepository.save(detallesCompra);

            total = total.add(subTotal);
        }

        // Procesar pagos
        for (PagoRequest pagoRequest : facturaRequest.getMediosPago()) {
            Pago pago = new Pago();
            pago.setCompra(compra);
            pago.setTipoPagoId(pagoRequest.getTipoPagoId());
            pago.setValor(BigDecimal.valueOf(pagoRequest.getValor()));
            pago.setTarjetaTipo(pagoRequest.getTipoTarjeta());
            pago.setCuotas(pagoRequest.getCuotas());
            pagoRepository.save(pago);
        }

        // Calcular impuestos
        BigDecimal impuestos = total.multiply(BigDecimal.valueOf(facturaRequest.getImpuesto())).divide(BigDecimal.valueOf(100));
        BigDecimal totalFinal = total.add(impuestos);

        // Guardar los impuestos y el total en la compra
        compra.setTotal(totalFinal);
        compra.setImpuestos(impuestos);
        compraRepository.save(compra);

        // Formatear la respuesta
        FacturaData facturaData = new FacturaData(compra.getId().toString(), totalFinal.toString(), compra.getFecha().toString());

        return new FacturaResponse("success", "La factura se ha creado correctamente con el número: " + compra.getId(), facturaData);
    }
}
