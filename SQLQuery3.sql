CREATE DATABASE MiCasitaSegura;
GO

USE MiCasitaSegura;
GO

-- Tabla de Roles
CREATE TABLE Roles (
    id_rol INT IDENTITY(1,1) PRIMARY KEY,
    nombre_rol NVARCHAR(50) NOT NULL UNIQUE,
    descripcion NVARCHAR(200),
    fecha_creacion DATETIME DEFAULT GETDATE(),
    activo BIT DEFAULT 1
);

-- Tabla de Usuarios
CREATE TABLE Usuarios (
    id_usuario INT IDENTITY(1,1) PRIMARY KEY,
    dpi NVARCHAR(20) NOT NULL UNIQUE,
    nombre NVARCHAR(100) NOT NULL,
    apellidos NVARCHAR(100) NOT NULL,
    correo NVARCHAR(150) NOT NULL UNIQUE,
    contrasena NVARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    lote NCHAR(1) NULL, -- A-Z, solo para residentes
    numero_casa INT NULL, -- 1-50, solo para residentes
    codigo_qr NVARCHAR(500) NULL, -- QR permanente
    fecha_creacion DATETIME DEFAULT GETDATE(),
    fecha_modificacion DATETIME DEFAULT GETDATE(),
    activo BIT DEFAULT 1,
    FOREIGN KEY (id_rol) REFERENCES Roles(id_rol)
);

-- Tabla de Tipos de Inconveniente
CREATE TABLE TiposInconveniente (
    id_tipo INT IDENTITY(1,1) PRIMARY KEY,
    nombre_tipo NVARCHAR(100) NOT NULL,
    descripcion NVARCHAR(200),
    activo BIT DEFAULT 1
);

-- Tabla de Reportes de Mantenimiento
CREATE TABLE ReportesMantenimiento (
    id_reporte INT IDENTITY(1,1) PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_tipo_inconveniente INT NOT NULL,
    descripcion NVARCHAR(500) NOT NULL,
    fecha_incidente DATETIME NOT NULL,
    fecha_reporte DATETIME DEFAULT GETDATE(),
    estado NVARCHAR(20) DEFAULT 'PENDIENTE', -- PENDIENTE, EN_PROCESO, RESUELTO
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_tipo_inconveniente) REFERENCES TiposInconveniente(id_tipo)
);

-- Tabla de Areas Comunes
CREATE TABLE AreasComunes (
    id_area INT IDENTITY(1,1) PRIMARY KEY,
    nombre_area NVARCHAR(100) NOT NULL,
    descripcion NVARCHAR(200),
    capacidad_maxima INT DEFAULT 0,
    activo BIT DEFAULT 1
);

-- Tabla de Reservas
CREATE TABLE Reservas (
    id_reserva INT IDENTITY(1,1) PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_area INT NOT NULL,
    fecha_reserva DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado NVARCHAR(20) DEFAULT 'ACTIVA', -- ACTIVA, CANCELADA, COMPLETADA
    fecha_creacion DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_area) REFERENCES AreasComunes(id_area)
);

-- Tabla de Tipos de Visita
CREATE TABLE TiposVisita (
    id_tipo_visita INT IDENTITY(1,1) PRIMARY KEY,
    nombre_tipo NVARCHAR(50) NOT NULL, -- VISITA, POR_INTENTOS
    descripcion NVARCHAR(200)
);

-- Tabla de Visitantes
CREATE TABLE Visitantes (
    id_visitante INT IDENTITY(1,1) PRIMARY KEY,
    nombre_visitante NVARCHAR(150) NOT NULL,
    dpi_visitante NVARCHAR(20),
    correo_visitante NVARCHAR(150),
    id_usuario_residente INT NOT NULL,
    id_tipo_visita INT NOT NULL,
    fecha_visita DATE NULL, -- Solo para tipo VISITA
    intentos_permitidos INT NULL, -- Solo para tipo POR_INTENTOS
    intentos_usados INT DEFAULT 0,
    codigo_qr NVARCHAR(500) NOT NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    activo BIT DEFAULT 1,
    FOREIGN KEY (id_usuario_residente) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_tipo_visita) REFERENCES TiposVisita(id_tipo_visita)
);

-- Tabla de Accesos (Log de entradas)
CREATE TABLE Accesos (
    id_acceso INT IDENTITY(1,1) PRIMARY KEY,
    codigo_qr NVARCHAR(500) NOT NULL,
    tipo_acceso NVARCHAR(20) NOT NULL, -- RESIDENTE, VISITANTE
    id_usuario INT NULL, -- Para residentes
    id_visitante INT NULL, -- Para visitantes
    fecha_acceso DATETIME DEFAULT GETDATE(),
    exitoso BIT NOT NULL,
    observaciones NVARCHAR(200),
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_visitante) REFERENCES Visitantes(id_visitante)
);

-- Tabla de Tipos de Pago
CREATE TABLE TiposPago (
    id_tipo_pago INT IDENTITY(1,1) PRIMARY KEY,
    nombre_tipo NVARCHAR(50) NOT NULL,
    monto_base DECIMAL(10,2) NOT NULL,
    descripcion NVARCHAR(200)
);

-- Tabla de Pagos
CREATE TABLE Pagos (
    id_pago INT IDENTITY(1,1) PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_tipo_pago INT NOT NULL,
    monto_pagado DECIMAL(10,2) NOT NULL,
    mora DECIMAL(10,2) DEFAULT 0,
    total DECIMAL(10,2) NOT NULL,
    observaciones NVARCHAR(300),
    numero_tarjeta NVARCHAR(20) NOT NULL, -- Encriptado
    nombre_titular NVARCHAR(150) NOT NULL,
    fecha_pago DATETIME DEFAULT GETDATE(),
    mes_correspondiente NVARCHAR(20), -- Para mantenimientos
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_tipo_pago) REFERENCES TiposPago(id_tipo_pago)
);

-- Tabla de Paqueteria
CREATE TABLE Paqueteria (
    id_paquete INT IDENTITY(1,1) PRIMARY KEY,
    numero_guia NVARCHAR(100) NOT NULL,
    id_usuario_destinatario INT NOT NULL,
    id_agente_registro INT NOT NULL,
    fecha_registro DATETIME DEFAULT GETDATE(),
    fecha_entrega DATETIME NULL,
    estado NVARCHAR(20) DEFAULT 'PENDIENTE', -- PENDIENTE, ENTREGADO
    FOREIGN KEY (id_usuario_destinatario) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_agente_registro) REFERENCES Usuarios(id_usuario)
);

-- Tabla de Tipos de Incidente
CREATE TABLE TiposIncidente (
    id_tipo_incidente INT IDENTITY(1,1) PRIMARY KEY,
    nombre_tipo NVARCHAR(100) NOT NULL,
    descripcion NVARCHAR(200)
);

-- Tabla de Incidentes
CREATE TABLE Incidentes (
    id_incidente INT IDENTITY(1,1) PRIMARY KEY,
    id_usuario_reporta INT NOT NULL,
    id_tipo_incidente INT NOT NULL,
    fecha_incidente DATETIME NOT NULL,
    descripcion NVARCHAR(200) NOT NULL,
    fecha_reporte DATETIME DEFAULT GETDATE(),
    estado NVARCHAR(20) DEFAULT 'REPORTADO', -- REPORTADO, EN_ATENCION, RESUELTO
    FOREIGN KEY (id_usuario_reporta) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_tipo_incidente) REFERENCES TiposIncidente(id_tipo_incidente)
);

-- Tabla de Conversaciones
CREATE TABLE Conversaciones (
    id_conversacion INT IDENTITY(1,1) PRIMARY KEY,
    id_usuario_residente INT NOT NULL,
    id_usuario_agente INT NOT NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    activa BIT DEFAULT 1,
    FOREIGN KEY (id_usuario_residente) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_usuario_agente) REFERENCES Usuarios(id_usuario)
);

-- Tabla de Mensajes
CREATE TABLE Mensajes (
    id_mensaje INT IDENTITY(1,1) PRIMARY KEY,
    id_conversacion INT NOT NULL,
    id_usuario_emisor INT NOT NULL,
    mensaje NVARCHAR(1000) NOT NULL,
    fecha_envio DATETIME DEFAULT GETDATE(),
    leido BIT DEFAULT 0,
    FOREIGN KEY (id_conversacion) REFERENCES Conversaciones(id_conversacion),
    FOREIGN KEY (id_usuario_emisor) REFERENCES Usuarios(id_usuario)
);

-- Insertar datos iniciales
INSERT INTO Roles (nombre_rol, descripcion) VALUES 
('Administrador de residencial', 'Administrador con acceso completo al sistema'),
('Agente de seguridad de residencial', 'Personal de seguridad con acceso limitado'),
('Residente', 'Residente del complejo habitacional');

INSERT INTO TiposInconveniente (nombre_tipo, descripcion) VALUES
('Lentitud en el sistema', 'El sistema responde muy lento'),
('Error al realizar una acción', 'Se produce un error al ejecutar alguna funcionalidad'),
('Error al acceder a una opción', 'No se puede acceder a determinada opción del menú'),
('Error de visualización', 'Problemas de visualización en la interfaz'),
('Otros', 'Otros tipos de inconvenientes no especificados');

INSERT INTO AreasComunes (nombre_area, descripcion) VALUES
('Salón', 'Salón de eventos para reuniones y celebraciones'),
('Piscina', 'Área de piscina para recreación');

INSERT INTO TiposVisita (nombre_tipo, descripcion) VALUES
('VISITA', 'Visita programada para fecha específica'),
('POR_INTENTOS', 'Visita con número limitado de intentos de acceso');

INSERT INTO TiposPago (nombre_tipo, monto_base, descripcion) VALUES
('Mantenimiento', 550.00, 'Pago mensual de mantenimiento'),
('Multa', 250.00, 'Multa por infracciones'),
('Reinstalación de servicios', 750.00, 'Costo de reinstalación de servicios');

INSERT INTO TiposIncidente (nombre_tipo, descripcion) VALUES
('Disturbios', 'Disturbios en el Área común'),
('Ruido', 'Ruido excesivo que molesta a otros residentes'),
('Accidente vehicular', 'Accidente de vehículos dentro del residencial'),
('Daños inmobiliarios', 'Daños a la propiedad común'),
('Otros', 'Otros tipos de incidentes');

-- �ndices para optimizar consultas
CREATE INDEX IX_Usuarios_DPI ON Usuarios(dpi);
CREATE INDEX IX_Usuarios_Correo ON Usuarios(correo);
CREATE INDEX IX_Usuarios_Rol ON Usuarios(id_rol);
CREATE INDEX IX_Reservas_Fecha ON Reservas(fecha_reserva, hora_inicio, hora_fin);
CREATE INDEX IX_Accesos_Fecha ON Accesos(fecha_acceso);
CREATE INDEX IX_Pagos_Usuario ON Pagos(id_usuario);
CREATE INDEX IX_Mensajes_Conversacion ON Mensajes(id_conversacion);

-- Constraints adicionales
ALTER TABLE Usuarios ADD CONSTRAINT CK_Lote_Range CHECK (lote >= 'A' AND lote <= 'Z');
ALTER TABLE Usuarios ADD CONSTRAINT CK_Casa_Range CHECK (numero_casa >= 1 AND numero_casa <= 50);
ALTER TABLE Reservas ADD CONSTRAINT CK_Hora_Valida CHECK (hora_fin > hora_inicio);
ALTER TABLE Visitantes ADD CONSTRAINT CK_Intentos_Positivos CHECK (intentos_permitidos > 0);

GO

