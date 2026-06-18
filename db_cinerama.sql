CREATE DATABASE IF NOT EXISTS db_cinerama;
USE db_cinerama;

CREATE TABLE Pelicula (
    id_pelicula INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    duracion_minutos INT NOT NULL
);

CREATE TABLE Cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(8) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    cancelaciones_acumuladas INT DEFAULT 0,
    es_frecuente TINYINT(1) DEFAULT 0
);

CREATE TABLE Calificacion (
    id_cliente INT NOT NULL,
    id_pelicula INT NOT NULL,
    puntuacion INT NOT NULL,
    fecha_calificacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_cliente, id_pelicula),
    CONSTRAINT fk_calif_cliente FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente),
    CONSTRAINT fk_calif_pelicula FOREIGN KEY (id_pelicula) REFERENCES Pelicula(id_pelicula),
    CONSTRAINT chk_puntuacion CHECK (puntuacion BETWEEN 1 AND 5)
);

CREATE TABLE Sala (
    id_sala INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    capacidad_total INT NOT NULL
);

CREATE TABLE Butaca (
    id_butaca INT AUTO_INCREMENT PRIMARY KEY,
    id_sala INT NOT NULL,
    fila VARCHAR(5) NOT NULL,
    numero INT NOT NULL,
    estado VARCHAR(20) DEFAULT 'Disponible' NOT NULL,
    CONSTRAINT fk_butaca_sala FOREIGN KEY (id_sala) REFERENCES Sala(id_sala),
    CONSTRAINT chk_estado_butaca CHECK (estado IN ('Disponible', 'Seleccionada', 'Vendida', 'Mantenimiento'))
);

CREATE TABLE Funcion (
    id_funcion INT AUTO_INCREMENT PRIMARY KEY,
    id_pelicula INT NOT NULL,
    id_sala INT NOT NULL,
    hora_inicio DATETIME NOT NULL,
    estado VARCHAR(20) DEFAULT 'Programada' NOT NULL,
    CONSTRAINT fk_funcion_pelicula FOREIGN KEY (id_pelicula) REFERENCES Pelicula(id_pelicula),
    CONSTRAINT fk_funcion_sala FOREIGN KEY (id_sala) REFERENCES Sala(id_sala),
    CONSTRAINT chk_estado_funcion CHECK (estado IN ('Programada', 'En curso', 'Finalizada', 'Cancelada'))
);

CREATE TABLE Transaccion (
    id_transaccion INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_funcion INT NOT NULL,
    monto_total DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(30) NOT NULL,
    estado VARCHAR(20) DEFAULT 'Pendiente' NOT NULL,
    codigo_qr VARCHAR(255) UNIQUE,
    fecha_transaccion DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_trans_cliente FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente),
    CONSTRAINT fk_trans_funcion FOREIGN KEY (id_funcion) REFERENCES Funcion(id_funcion),
    CONSTRAINT chk_monto_positivo CHECK (monto_total > 0),
    CONSTRAINT chk_metodo_pago CHECK (metodo_pago IN ('Efectivo', 'Tarjeta Visa', 'Tarjeta Mastercard', 'Yape', 'Plin'))
);

CREATE TABLE Boleto_Detalle (
    id_boleto INT AUTO_INCREMENT PRIMARY KEY,
    id_transaccion INT NOT NULL,
    id_butaca INT NOT NULL,
    precio_aplicado DECIMAL(10,2) NOT NULL,
    qr_usado TINYINT(1) DEFAULT 0,
    CONSTRAINT fk_boleto_transaccion FOREIGN KEY (id_transaccion) REFERENCES Transaccion(id_transaccion),
    CONSTRAINT fk_boleto_butaca FOREIGN KEY (id_butaca) REFERENCES Butaca(id_butaca)
);

CREATE TABLE Incidencia (
    id_incidencia INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    id_sala INT,
    id_funcion INT,
    fecha_reporte DATETIME DEFAULT CURRENT_TIMESTAMP,
    reportado_por VARCHAR(100),
    estado VARCHAR(20) DEFAULT 'Sin atender',
    CONSTRAINT fk_incidencia_sala FOREIGN KEY (id_sala) REFERENCES Sala(id_sala),
    CONSTRAINT fk_incidencia_funcion FOREIGN KEY (id_funcion) REFERENCES Funcion(id_funcion),
    CONSTRAINT chk_tipo_incidencia CHECK (tipo IN ('Error en QR', 'Conflicto de asientos', 'Fallo tecnico'))
);
