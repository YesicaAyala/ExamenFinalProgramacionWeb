-- Datos para tipo_documento
INSERT INTO tipo_documento (id, nombre) VALUES 
(1, 'CC'), 
(2, 'NIT'), 
(3, 'PASAPORTE');

-- Datos para cliente
INSERT INTO cliente (id, nombre, documento, tipo_documento_id) VALUES
(1, 'Juan Pérez', '10000001', 1),
(2, 'María López', '20000002', 2);

-- Datos para tipo_producto
INSERT INTO tipo_producto (id, nombre) VALUES 
(1, 'Electrónica'),
(2, 'Ropa'),
(3, 'Hogar');

-- Datos para producto
INSERT INTO producto (id, nombre, descripcion, precio, tipo_producto_id, cantidad, referencia) VALUES
(1, 'Televisor', 'Televisor LED de 50 pulgadas', 1500.00, 1, 10, 'ELEC001'),
(2, 'Laptop', 'Laptop de 16GB RAM y 512GB SSD', 2500.00, 1, 5, 'ELEC002'),
(3, 'Celular', 'Smartphone con cámara de alta resolución', 800.00, 1, 20, 'ELEC003');

-- Datos para tienda
INSERT INTO tienda (id, nombre, direccion, uuid) VALUES
(1, 'Tienda Principal', 'Calle 123, Ciudad', '123e4567-e89b-12d3-a456-426614174000'),
(2, 'Sucursal Norte', 'Avenida 45, Ciudad', '123e4567-e89b-12d3-a456-426614174001');

-- Datos para vendedor
INSERT INTO vendedor (id, nombre, documento, email) VALUES
(1, 'Carlos Gómez', '10101010', 'carlos.gomez@example.com'),
(2, 'Ana Torres', '20202020', 'ana.torres@example.com');

-- Datos para cajero
INSERT INTO cajero (id, nombre, documento, tienda_id, email, token) VALUES
(1, 'Luis Martínez', '30303030', 1, 'luis.martinez@example.com', 'token123'),
(2, 'Sofía Reyes', '40404040', 2, 'sofia.reyes@example.com', 'token456');

-- Datos para tipo_pago
INSERT INTO tipo_pago (id, nombre) VALUES
(1, 'TARJETA CREDITO'),
(2, 'TARJETA DEBITO'),
(3, 'BITCOIN'),
(4, 'EFECTIVO');

-- Datos para compra (puedes añadir datos adicionales si es necesario)
INSERT INTO compra (id, cliente_id, tienda_id, vendedor_id, cajero_id, total, impuestos, fecha, observaciones) VALUES
(1, 1, 1, 1, 1, 2300.00, 5.00, NOW(), 'Compra inicial de prueba');

-- Datos para detalles_compra
INSERT INTO detalles_compra (id, compra_id, producto_id, cantidad, precio, descuento) VALUES
(1, 1, 1, 2, 1500.00, 10.00),
(2, 1, 2, 1, 2500.00, 0.00);

-- Datos para pago
INSERT INTO pago (id, compra_id, tipo_pago_id, tarjeta_tipo, valor, cuotas) VALUES
(1, 1, 1, 'MASTERCARD', 2000.00, 1),
(2, 1, 3, NULL, 300.00, NULL);
