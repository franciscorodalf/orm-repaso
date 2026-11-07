PRAGMA foreign_keys=ON;
CREATE TABLE IF NOT EXISTS socios (
 id_socio INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL,
 email TEXT NOT NULL UNIQUE, telefono TEXT, fecha_alta TEXT NOT NULL,
 activo INTEGER NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);
CREATE TABLE IF NOT EXISTS autores (
 id_autor INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL,
 nacionalidad TEXT, anio_nacimiento INTEGER
);
CREATE TABLE IF NOT EXISTS libros (
 id_libro INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT NOT NULL,
 isbn TEXT NOT NULL UNIQUE, genero TEXT NOT NULL, anio_publicacion INTEGER,
 ejemplares INTEGER NOT NULL DEFAULT 1 CHECK (ejemplares>=0),
 id_autor INTEGER NOT NULL REFERENCES autores(id_autor) ON DELETE RESTRICT
);
CREATE TABLE IF NOT EXISTS prestamos (
 id_prestamo INTEGER PRIMARY KEY AUTOINCREMENT,
 id_socio INTEGER NOT NULL REFERENCES socios(id_socio) ON DELETE RESTRICT,
 id_libro INTEGER NOT NULL REFERENCES libros(id_libro) ON DELETE RESTRICT,
 fecha_prestamo TEXT NOT NULL, fecha_limite TEXT NOT NULL,
 fecha_devolucion TEXT,
 CHECK(fecha_limite>=fecha_prestamo),
 CHECK(fecha_devolucion IS NULL OR fecha_devolucion>=fecha_prestamo)
);
CREATE TABLE IF NOT EXISTS reservas (
 id_reserva INTEGER PRIMARY KEY AUTOINCREMENT,
 id_socio INTEGER NOT NULL REFERENCES socios(id_socio) ON DELETE RESTRICT,
 id_libro INTEGER NOT NULL REFERENCES libros(id_libro) ON DELETE RESTRICT,
 fecha_reserva TEXT NOT NULL,
 estado TEXT NOT NULL DEFAULT 'PENDIENTE' CHECK(estado IN ('PENDIENTE','ATENDIDA','CANCELADA'))
);
