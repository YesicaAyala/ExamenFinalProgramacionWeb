package com.example.demo.services;

import com.example.demo.dto.FacturaResponse;
import com.example.demo.dto.PagoRequest;
import com.example.demo.dto.ProductoRequest;
import com.example.demo.dto.FacturaRequest;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class FacturaService {
	@Autowired
    private TiendaRepository tiendaRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private VendedorRepository vendedorRepository;

    @Autowired
    private CajeroRepository cajeroRepository;


    public FacturaResponse procesarFactura(Long tiendaId, FacturaRequest facturaRequest) {
        // Lógica para procesar la factura
        Tienda tienda = tiendaRepository.findById(tiendaId).orElseThrow(() -> new RuntimeException("Tienda no encontrada"));

        // Buscar el cliente
        Cliente cliente = clienteRepository.findByDocumento(facturaRequest.getCliente().getDocumento());

        // Procesar productos
        List<Producto> productos = new ArrayList<>();
        for (ProductoRequest productoRequest : facturaRequest.getProductos()) {
            Producto producto = productoRepository.findByReferencia(productoRequest.getReferencia());
            productos.add(producto);
        }

        // Procesar pagos
        List<Pago> pagos = new ArrayList<>();
        for (PagoRequest pagoRequest : facturaRequest.getMediosPago()) {
            Pago pago = new Pago();
            pago.setValor(pagoRequest.getValor());
            pagos.add(pago);
        }

        // Crear la factura
        Factura factura = new Factura();
        factura.setCliente(cliente);
        factura.setTienda(tienda);
        factura.setProductos(productos);
        factura.setPagos(pagos);
        facturaRepository.save(factura);

        // Crear la respuesta
        FacturaResponse response = new FacturaResponse();
        response.setStatus("success");
        response.setMessage("La factura se ha creado correctamente con el número: " + factura.getId());
        FacturaResponse.FacturaData data = new FacturaResponse.FacturaData();
        data.setNumero(factura.getId().toString());
        data.setTotal("7728"); // Total calculado, según la lógica de tu negocio
        data.setFecha("2024-01-01"); // Fecha de la factura
        response.setData(data);

        return response;
    }
}
