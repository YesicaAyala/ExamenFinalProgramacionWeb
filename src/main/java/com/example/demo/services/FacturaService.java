package com.example.demo.services;

import com.example.demo.dto.FacturaResponse;
import com.example.demo.dto.PagoRequest;
import com.example.demo.dto.ProductoRequest;
import com.example.demo.dto.FacturaRequest;
import com.example.demo.entities.*;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FacturaService {

    private final ClienteRepository clienteRepository;
    private final TiendaRepository tiendaRepository;
    private final VendedorRepository vendedorRepository;
    private final CajeroRepository cajeroRepository;
    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detallesCompraRepository;
    private final PagoRepository pagoRepository;
    private final ProductoRepository productoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final TipoPagoRepository tipoPagoRepository;

    public FacturaService(ClienteRepository clienteRepository, TiendaRepository tiendaRepository,
                          VendedorRepository vendedorRepository, CajeroRepository cajeroRepository,
                          CompraRepository compraRepository, DetalleCompraRepository detallesCompraRepository,
                          PagoRepository pagoRepository, ProductoRepository productoRepository, TipoDocumentoRepository tipoDocumentoRepository, TipoPagoRepository tipoPagoRepository) {
        this.clienteRepository = clienteRepository;
        this.tiendaRepository = tiendaRepository;
        this.vendedorRepository = vendedorRepository;
        this.cajeroRepository = cajeroRepository;
        this.compraRepository = compraRepository;
        this.detallesCompraRepository = detallesCompraRepository;
        this.pagoRepository = pagoRepository;
        this.productoRepository = productoRepository;
		this.tipoDocumentoRepository = tipoDocumentoRepository;
		this.tipoPagoRepository = tipoPagoRepository;
    }

    public FacturaResponse procesarFactura(String tiendaUuid, FacturaRequest facturaRequest) {
        
    	System.out.println(facturaRequest.toString());
    	// Buscar la tienda
        Tienda tienda = tiendaRepository.findByUuid(tiendaUuid).orElseThrow(() ->
                new NotFoundException("Tienda no encontrada"));

        // Registrar o buscar cliente
        Cliente cliente = clienteRepository.findByDocumentoAndTipoDocumento(
                facturaRequest.getCliente().getDocumento(), facturaRequest.getCliente().getTipoDocumento())
                .orElseGet(() -> {
                    Cliente nuevoCliente = new Cliente();
                    nuevoCliente.setNombre(facturaRequest.getCliente().getNombre());
                    nuevoCliente.setDocumento(facturaRequest.getCliente().getDocumento());
                    TipoDocumento tipoDocumento = tipoDocumentoRepository.findByNombre(facturaRequest.getCliente().getTipoDocumento());
                    
                    nuevoCliente.setTipoDocumento(tipoDocumento);
                    return clienteRepository.save(nuevoCliente);
                });

        // Buscar vendedor y cajero
        Vendedor vendedor = vendedorRepository.findByDocumento(facturaRequest.getVendedor().getDocumento())
                .orElseThrow(() -> new NotFoundException("Vendedor no encontrado"));

        Cajero cajero = cajeroRepository.findByToken(
                facturaRequest.getCajero().getToken())
                .orElseThrow(() -> new NotFoundException("Cajero no encontrado para la tienda especificada"));

        // Crear la compra
        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setTienda(tienda);
        compra.setVendedor(vendedor);
        compra.setCajero(cajero);
        compra.setImpuestos(facturaRequest.getImpuesto());
        compra.setFecha(LocalDateTime.now());
        double total = 0;
        compra.setTotal(total);
        compraRepository.save(compra);
        for (ProductoRequest productoRequest : facturaRequest.getProductos()) {
            Producto producto = productoRepository.findByReferencia(productoRequest.getReferencia())
                    .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

            double precioFinal = producto.getPrecio() * productoRequest.getCantidad();
            precioFinal -= precioFinal * (productoRequest.getDescuento() / 100);

            DetallesCompra detalles = new DetallesCompra();
            detalles.setCompra(compra);
            detalles.setProducto(producto);
            detalles.setCantidad(productoRequest.getCantidad());
            detalles.setPrecio(producto.getPrecio());
            detalles.setDescuento((double) productoRequest.getDescuento());
            detallesCompraRepository.save(detalles);

            total += precioFinal;
        }

        compra.setTotal(total);
        compraRepository.save(compra);

        
        // Registrar pagos
        for (PagoRequest pagoRequest : facturaRequest.getMedios_pago()) {
            TipoPago tipoPago = tipoPagoRepository.findByNombre(pagoRequest.getTipo_pago())
                    .orElseThrow(() -> new NotFoundException("Tipo de pago no encontrado"));
            System.out.println(tipoPago.toString());
            Pago pago = new Pago();
            pago.setCompra(compra);
            pago.setTipoPago(tipoPago);
            pago.setTarjetaTipo(pagoRequest.getTipo_tarjeta());
            pago.setValor(pagoRequest.getValor());
            pago.setCuotas(pagoRequest.getCuotas());
            pagoRepository.save(pago);
        }

        // Crear respuesta
        FacturaResponse response = new FacturaResponse();
        response.setStatus("success");
        response.setMessage("La factura se ha creado correctamente con el número: " + compra.getId());

        FacturaResponse.FacturaData data = new FacturaResponse.FacturaData();
        data.setNumero(String.valueOf(compra.getId()));
        data.setTotal(String.valueOf(total));
        data.setFecha(compra.getFecha().toString());

        response.setData(data);

        return response;
    }
}


