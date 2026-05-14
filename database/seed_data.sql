-- Datos iniciales para pruebas de GymManager

-- Rutinas Predeterminadas
INSERT INTO rutinas (nombre_rutina, descripcion, fecha_asignacion, cliente_id) VALUES 
('Empuje (Push)', 'Enfocada en Pecho, Hombro y Tríceps', NOW(), 1),
('Tirón (Pull)', 'Enfocada en Espalda y Bíceps', NOW(), 1),
('Pierna (Legs)', 'Enfocada en Cuádriceps, Isquios y Gemelo', NOW(), 1),
('Cuerpo Completo (Full Body)', 'Rutina de mantenimiento general', NOW(), 1);

-- Ejercicios para la rutina de Empuje (ID 1)
INSERT INTO ejercicios (nombre, series, repeticiones, peso, rutina_id) VALUES 
('Press de Banca con Barra', 4, 10, 60.00, 1),
('Press Militar con Mancuernas', 3, 12, 10.00, 1),
('Aperturas en Polea', 3, 15, 15.00, 1),
('Extensiones de Tríceps Polea Alta', 3, 12, 20.00, 1);

-- Ejercicios para la rutina de Tirón (ID 2)
INSERT INTO ejercicios (nombre, series, repeticiones, peso, rutina_id) VALUES 
('Dominadas (o Jalón al Pecho)', 4, 10, 0.00, 2),
('Remo con Barra', 3, 12, 40.00, 2),
('Facepull', 3, 15, 10.00, 2),
('Curl de Bíceps con Barra EZ', 3, 12, 20.00, 2);

-- Ejercicios para la rutina de Pierna (ID 3)
INSERT INTO ejercicios (nombre, series, repeticiones, peso, rutina_id) VALUES 
('Sentadilla con Barra Libre', 4, 8, 80.00, 3),
('Prensa de Piernas', 3, 12, 120.00, 3),
('Curl Femoral Tumbado', 3, 12, 30.00, 3),
('Elevación de Talones en Máquina', 4, 15, 40.00, 3);
