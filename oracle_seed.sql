-- Seed data for Cinerama
-- Run after oracle_ddl.sql

DELETE FROM Compra;
DELETE FROM Incidencia;
DELETE FROM Calificacion;
DELETE FROM Butaca;
DELETE FROM Funcion;
DELETE FROM Producto;
DELETE FROM Sala;
DELETE FROM Cliente;
DELETE FROM Pelicula;

-- Peliculas
INSERT INTO Pelicula (titulo, duracion_minutos, genero, sinopsis) VALUES ('El Padrino', 175, 'Drama', 'La historia de la familia mafiosa Corleone.');
INSERT INTO Pelicula (titulo, duracion_minutos, genero, sinopsis) VALUES ('Interestelar', 169, 'Ciencia Ficcion', 'Un viaje interestelar para salvar la humanidad.');
INSERT INTO Pelicula (titulo, duracion_minutos, genero, sinopsis) VALUES ('El Senor de los Anillos', 201, 'Fantasia', 'La busqueda del anillo unico.');
INSERT INTO Pelicula (titulo, duracion_minutos, genero, sinopsis) VALUES ('Toy Story', 81, 'Animacion', 'Los juguetes cobran vida.');
INSERT INTO Pelicula (titulo, duracion_minutos, genero, sinopsis) VALUES ('Parasitos', 132, 'Suspenso', 'Una familia pobre se infiltra en una familia rica.');

-- Clientes (admin + usuarios)
INSERT INTO Cliente (dni, nombre, email, telefono) VALUES ('00000000', 'admin', 'admin@cinerama.com', '999000000');
INSERT INTO Cliente (dni, nombre, email, telefono) VALUES ('12345678', 'Carlos Perez', 'carlos@email.com', '987654321');
INSERT INTO Cliente (dni, nombre, email, telefono) VALUES ('87654321', 'Maria Gomez', 'maria@email.com', '987654322');

-- Salas con diferentes capacidades
INSERT INTO Sala (nombre, tipo, capacidad_total) VALUES ('Sala Premium', 'vip', 30);
INSERT INTO Sala (nombre, tipo, capacidad_total) VALUES ('Sala Principal', '2d', 80);
INSERT INTO Sala (nombre, tipo, capacidad_total) VALUES ('Sala 3D', '3d', 60);
INSERT INTO Sala (nombre, tipo, capacidad_total) VALUES ('Sala IMAX', 'imax', 50);

-- Butacas via PL/SQL
BEGIN
  -- Sala 1 (Premium): 30 butacas, filas A-C x 10
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'A', i, 'Disponible' FROM Sala WHERE nombre='Sala Premium';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'B', i, 'Disponible' FROM Sala WHERE nombre='Sala Premium';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'C', i, 'Disponible' FROM Sala WHERE nombre='Sala Premium';
  END LOOP;

  -- Sala 2 (Principal): 80 butacas, filas A-H x 10
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'A', i, 'Disponible' FROM Sala WHERE nombre='Sala Principal';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'B', i, 'Disponible' FROM Sala WHERE nombre='Sala Principal';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'C', i, 'Disponible' FROM Sala WHERE nombre='Sala Principal';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'D', i, 'Disponible' FROM Sala WHERE nombre='Sala Principal';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'E', i, 'Disponible' FROM Sala WHERE nombre='Sala Principal';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'F', i, 'Disponible' FROM Sala WHERE nombre='Sala Principal';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'G', i, 'Disponible' FROM Sala WHERE nombre='Sala Principal';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'H', i, 'Disponible' FROM Sala WHERE nombre='Sala Principal';
  END LOOP;

  -- Sala 3 (3D): 60 butacas, filas A-F x 10
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'A', i, 'Disponible' FROM Sala WHERE nombre='Sala 3D';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'B', i, 'Disponible' FROM Sala WHERE nombre='Sala 3D';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'C', i, 'Disponible' FROM Sala WHERE nombre='Sala 3D';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'D', i, 'Disponible' FROM Sala WHERE nombre='Sala 3D';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'E', i, 'Disponible' FROM Sala WHERE nombre='Sala 3D';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'F', i, 'Disponible' FROM Sala WHERE nombre='Sala 3D';
  END LOOP;

  -- Sala 4 (IMAX): 50 butacas, filas A-E x 10
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'A', i, 'Disponible' FROM Sala WHERE nombre='Sala IMAX';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'B', i, 'Disponible' FROM Sala WHERE nombre='Sala IMAX';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'C', i, 'Disponible' FROM Sala WHERE nombre='Sala IMAX';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'D', i, 'Disponible' FROM Sala WHERE nombre='Sala IMAX';
  END LOOP;
  FOR i IN 1..10 LOOP
    INSERT INTO Butaca (id_sala, fila, numero, estado)
      SELECT id_sala, 'E', i, 'Disponible' FROM Sala WHERE nombre='Sala IMAX';
  END LOOP;
END;
/

-- Productos
INSERT INTO Producto (nombre, descripcion, precio, categoria, activo) VALUES ('Cancha Grande', 'Cancha de maiz gigante', 12.00, 'Comida', 1);
INSERT INTO Producto (nombre, descripcion, precio, categoria, activo) VALUES ('Cancha Chica', 'Cancha de maiz pequeña', 8.00, 'Comida', 1);
INSERT INTO Producto (nombre, descripcion, precio, categoria, activo) VALUES ('Gaseosa 1L', 'Gaseosa de 1 litro', 10.00, 'Bebida', 1);
INSERT INTO Producto (nombre, descripcion, precio, categoria, activo) VALUES ('Agua 500ml', 'Agua mineral', 5.00, 'Bebida', 1);
INSERT INTO Producto (nombre, descripcion, precio, categoria, activo) VALUES ('Combo Duo', '2 canchas + 2 gaseosas', 30.00, 'Combo', 1);
INSERT INTO Producto (nombre, descripcion, precio, categoria, activo) VALUES ('Combo Personal', '1 cancha + 1 gaseosa', 18.00, 'Combo', 1);

COMMIT;
EXIT;
