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
        // Validaciones previas
        if (facturaRequest.getCliente() == null) {
            return crearRespuestaError("No hay información del cliente", 404, 0.05);
        }

        if (facturaRequest.getVendedor() == null) {
            return crearRespuestaError("No hay información del vendedor", 404, 0.05);
        }

        if (facturaRequest.getCajero() == null) {
            return crearRespuestaError("No hay información del cajero", 404, 0.05);
        }

        if (facturaRequest.getProductos() == null || facturaRequest.getProductos().isEmpty()) {
            return crearRespuestaError("No hay productos asignados para esta compra", 404, 0.05);
        }

        if (facturaRequest.getMedios_pago() == null || facturaRequest.getMedios_pago().isEmpty()) {
            return crearRespuestaError("No hay medios de pagos asignados para esta compra", 404, 0.05);
        }

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

        // Buscar vendedor y validar que esté en la tienda
        Vendedor vendedor = vendedorRepository.findByDocumento(facturaRequest.getVendedor().getDocumento())
                .orElseThrow(() -> new NotFoundException("El vendedor no existe en la tienda"));

        // Buscar cajero y validar token
        Cajero cajero = cajeroRepository.findByToken(facturaRequest.getCajero().getToken())
                .orElseThrow(() -> new NotFoundException("El token no corresponde a ningún cajero en la tienda"));

        // Verificar si el token del cajero está asignado a otra tienda
        if (!cajero.getTienda().getUuid().equals(tiendaUuid)) {
            return crearRespuestaError("El cajero no está asignado a esta tienda", 403, 0.15);
        }

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

        // Procesar los productos
        for (ProductoRequest productoRequest : facturaRequest.getProductos()) {
            Producto producto = productoRepository.findByReferencia(productoRequest.getReferencia())
                    .orElseThrow(() -> new NotFoundException("La referencia del producto " + productoRequest.getReferencia() + " no existe, por favor revisar los datos"));

            if (producto.getCantidad() < productoRequest.getCantidad()) {
                return crearRespuestaError("La cantidad a comprar supera el máximo del producto en tienda", 403, 0.1);
            }

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

        // Procesar los pagos
        for (PagoRequest pagoRequest : facturaRequest.getMedios_pago()) {
            TipoPago tipoPago = tipoPagoRepository.findByNombre(pagoRequest.getTipo_pago())
                    .orElseThrow(() -> new NotFoundException("Tipo de pago no permitido en la tienda"));

            Pago pago = new Pago();
            pago.setCompra(compra);
            pago.setTipoPago(tipoPago);
            pago.setTarjetaTipo(pagoRequest.getTipo_tarjeta());
            pago.setValor(pagoRequest.getValor());
            pago.setCuotas(pagoRequest.getCuotas());
            pagoRepository.save(pago);
        }

        // Verificar si el valor de la factura coincide con el total de los pagos
        double totalPagado = pagoRepository.sumTotalByCompra(compra.getId());
        if (total != totalPagado) {
            return crearRespuestaError("El valor de la factura no coincide con el valor total de los pagos", 403, 0.2);
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

    private FacturaResponse crearRespuestaError(String message, int status, double valor) {
        FacturaResponse response = new FacturaResponse();
        response.setStatus(""+status);
        response.setMessage(message);
        return response;
    }

}


