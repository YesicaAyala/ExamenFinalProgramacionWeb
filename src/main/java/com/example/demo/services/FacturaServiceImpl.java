package com.example.demo.services;

import com.example.demo.entities.*;
import com.example.demo.models.FacturaRequest;
import com.example.demo.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FacturaServiceImpl implements FacturaService {

    private final TiendaRepository tiendaRepository;
    private final ClienteRepository clienteRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final ProductoRepository productoRepository;
    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final VendedorRepository vendedorRepository;
    private final CajeroRepository cajeroRepository;
    private final PagoRepository pagoRepository;
    private final TipoPagoRepository tipoPagoRepository;

    public FacturaServiceImpl(
        TiendaRepository tiendaRepository,
        ClienteRepository clienteRepository,
        TipoDocumentoRepository tipoDocumentoRepository,
        ProductoRepository productoRepository,
        CompraRepository compraRepository,
        DetalleCompraRepository detalleCompraRepository,
        VendedorRepository vendedorRepository,
        CajeroRepository cajeroRepository,
        PagoRepository pagoRepository,
        TipoPagoRepository tipoPagoRepository) {
        this.tiendaRepository = tiendaRepository;
        this.clienteRepository = clienteRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.productoRepository = productoRepository;
        this.compraRepository = compraRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.vendedorRepository = vendedorRepository;
        this.cajeroRepository = cajeroRepository;
        this.pagoRepository = pagoRepository;
        this.tipoPagoRepository = tipoPagoRepository;
    }

    @Transactional
    public Compra procesarFactura(FacturaRequest facturaRequest, Long tiendaId) {
        // Validar existencia de la tienda
        Tienda tienda = tiendaRepository.findById(tiendaId)
                .orElseThrow(() -> new IllegalArgumentException("Tienda no encontrada"));

        // Validar cliente o crear si no existe
        Optional<Cliente> clienteOpt = clienteRepository.findByDocumento(facturaRequest.getCliente().getDocumento());
        Cliente cliente = clienteOpt.orElseGet(() -> {
            TipoDocumento tipoDocumento = tipoDocumentoRepository
                .findByNombre(facturaRequest.getCliente().getTipoDocumento())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de documento no encontrado"));
            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setNombre(facturaRequest.getCliente().getNombre());
            nuevoCliente.setDocumento(facturaRequest.getCliente().getDocumento());
            nuevoCliente.setTipoDocumento(tipoDocumento);
            return clienteRepository.save(nuevoCliente);
        });

        // Validar vendedor
        Vendedor vendedor = vendedorRepository.findByDocumento(facturaRequest.getVendedor().getDocumento())
                .orElseThrow(() -> new IllegalArgumentException("Vendedor no encontrado"));

        // Validar cajero
        Cajero cajero = cajeroRepository.findByToken(facturaRequest.getCajero().getToken())
                .orElseThrow(() -> new IllegalArgumentException("Cajero no encontrado"));

        // Crear la compra
        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setTienda(tienda);
        compra.setVendedor(vendedor);
        compra.setCajero(cajero);
        compra.setImpuestos(facturaRequest.getImpuesto());
        compra.setTotal(0.0);
        compra = compraRepository.save(compra);

        // Procesar detalles de la compra
        Double total = 0.0;
        for (FacturaRequest.ProductoDTO productoDTO : facturaRequest.getProductos()) {
            Producto producto = productoRepository.findByReferencia(productoDTO.getReferencia())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidad(productoDTO.getCantidad());
            detalle.setPrecio(producto.getPrecio());
            detalle.setDescuento(productoDTO.getDescuento());
            total += (producto.getPrecio() * productoDTO.getCantidad()) - productoDTO.getDescuento();
            detalleCompraRepository.save(detalle);
        }
        compra.setTotal(total);
        compraRepository.save(compra);

        // Procesar medios de pago
        for (FacturaRequest.MedioPagoDTO medioPagoDTO : facturaRequest.getMediosPago()) {
            TipoPago tipoPago = tipoPagoRepository.findByNombre(medioPagoDTO.getTipoPago())
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de pago no encontrado"));
            Pago pago = new Pago();
            pago.setCompra(compra);
            pago.setTipoPago(tipoPago);
            pago.setTarjetaTipo(medioPagoDTO.getTipoTarjeta());
            pago.setValor(medioPagoDTO.getValor());
            pago.setCuotas(medioPagoDTO.getCuotas());
            pagoRepository.save(pago);
        }

        return compra;
    }
}
