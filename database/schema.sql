CREATE DATABASE IF NOT EXISTS gym_manager;
USE gym_manager;

-- Admin users for the system
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) DEFAULT 'CLIENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Clients of the gym
CREATE TABLE IF NOT EXISTS clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    fecha_alta DATE DEFAULT (CURRENT_DATE),
    estado ENUM('ACTIVO', 'INACTIVO') DEFAULT 'ACTIVO',
    observaciones TEXT
);

-- Payment records
CREATE TABLE IF NOT EXISTS pagos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    monto DECIMAL(10, 2) NOT NULL,
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    concepto VARCHAR(100) DEFAULT 'Cuota Mensual',
    metodo_pago VARCHAR(50),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);

-- Training routines
CREATE TABLE IF NOT EXISTS rutinas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    nombre_rutina VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fecha_asignacion DATE DEFAULT (CURRENT_DATE),
    dias_semana INT,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);

-- Exercises within a routine
CREATE TABLE IF NOT EXISTS ejercicios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rutina_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    series INT,
    repeticiones INT,
    peso DECIMAL(5, 2),
    FOREIGN KEY (rutina_id) REFERENCES rutinas(id) ON DELETE CASCADE
);

-- Insert a default admin (password: admin123 - hashed version should be used in real app)
-- Inserting with plain text for now, but will be handled by Spring Security later
INSERT INTO usuarios (username, password, email) VALUES ('admin', 'admin123', 'admin@gymmanager.com');
