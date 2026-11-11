-- CINEFEST. Completa C01 a C30. No se entregan las soluciones.

-- C01
SELECT titulo, genero, anio
FROM peliculas
ORDER BY titulo ASC;

-- C02
SELECT nombre, fecha_alta
FROM espectadores
WHERE activo = 1
ORDER BY fecha_alta ASC, nombre ASC;

-- C03
SELECT titulo, pais, anio
FROM peliculas
WHERE genero = 'Drama'
ORDER BY titulo ASC;

-- C04
SELECT fecha, hora, id_sesion, precio
FROM sesiones
WHERE fecha BETWEEN '2026-11-13' AND '2026-11-14'
ORDER BY fecha ASC, hora ASC, id_sesion ASC;

-- C05
SELECT DISTINCT genero
FROM peliculas
ORDER BY genero ASC;

-- C06
SELECT se.nombre AS sede, sa.nombre AS sala, sa.aforo
FROM salas sa
JOIN sedes se ON se.id_sede = sa.id_sede
ORDER BY sede ASC, sala ASC;

-- C07
SELECT s.id_sesion, p.titulo AS pelicula, se.nombre AS sede, sa.nombre AS sala, s.fecha, s.hora
FROM sesiones s
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
JOIN salas sa ON sa.id_sala = s.id_sala
JOIN sedes se ON se.id_sede = sa.id_sede
ORDER BY s.id_sesion ASC;

-- C08
SELECT e.id_entrada, es.nombre AS espectador, p.titulo AS pelicula, e.estado
FROM entradas e
JOIN espectadores es ON es.id_espectador = e.id_espectador
JOIN sesiones s ON s.id_sesion = e.id_sesion
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
ORDER BY e.id_entrada ASC;

-- C09
SELECT es.nombre, es.email
FROM espectadores es
WHERE NOT EXISTS (SELECT 1 FROM entradas e WHERE e.id_espectador = es.id_espectador)
ORDER BY es.nombre ASC;

-- C10
SELECT p.titulo AS pelicula, es.nombre AS espectador, v.puntuacion
FROM valoraciones v
JOIN peliculas p ON p.id_pelicula = v.id_pelicula
JOIN espectadores es ON es.id_espectador = v.id_espectador
ORDER BY pelicula ASC, espectador ASC;

-- C11
SELECT genero, COUNT(*) AS total_peliculas
FROM peliculas
GROUP BY genero
ORDER BY genero ASC;

-- C12
SELECT se.nombre AS sede, COUNT(sa.id_sala) AS total_salas, COALESCE(SUM(sa.aforo), 0) AS aforo_total
FROM sedes se
LEFT JOIN salas sa ON sa.id_sede = se.id_sede
GROUP BY se.id_sede, se.nombre
ORDER BY sede ASC;

-- C13
SELECT es.nombre AS espectador, COUNT(e.id_entrada) AS total_entradas
FROM espectadores es
LEFT JOIN entradas e ON e.id_espectador = es.id_espectador AND e.estado IN ('PAGADA', 'USADA')
GROUP BY es.id_espectador, es.nombre
ORDER BY total_entradas DESC, espectador ASC;

-- C14
SELECT p.titulo AS pelicula, COUNT(v.id_valoracion) AS numero_valoraciones, ROUND(AVG(v.puntuacion), 2) AS media
FROM peliculas p
JOIN valoraciones v ON v.id_pelicula = p.id_pelicula
GROUP BY p.id_pelicula, p.titulo
HAVING COUNT(v.id_valoracion) >= 2
ORDER BY media DESC, pelicula ASC;

-- C15
SELECT s.id_sesion, sa.aforo, COUNT(e.id_entrada) AS vendidas, sa.aforo - COUNT(e.id_entrada) AS libres
FROM sesiones s
JOIN salas sa ON sa.id_sala = s.id_sala
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA', 'USADA')
GROUP BY s.id_sesion, sa.aforo
ORDER BY s.id_sesion ASC;

-- C16
SELECT p.titulo
FROM peliculas p
WHERE NOT EXISTS (SELECT 1 FROM sesiones s WHERE s.id_pelicula = p.id_pelicula)
ORDER BY p.titulo ASC;

-- C17
SELECT s.id_sesion, p.titulo AS pelicula, s.fecha
FROM sesiones s
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
WHERE s.estado = 'PROGRAMADA'
  AND NOT EXISTS (SELECT 1 FROM entradas e WHERE e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA', 'USADA'))
ORDER BY s.id_sesion ASC;

-- C18
SELECT s.id_sesion, p.titulo AS pelicula, sa.aforo
FROM sesiones s
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
JOIN salas sa ON sa.id_sala = s.id_sala
JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA', 'USADA')
GROUP BY s.id_sesion, p.titulo, sa.aforo
HAVING COUNT(e.id_entrada) >= sa.aforo
ORDER BY s.id_sesion ASC;

-- C19
SELECT p.titulo AS pelicula, COUNT(e.id_entrada) AS entradas_validas
FROM peliculas p
JOIN sesiones s ON s.id_pelicula = p.id_pelicula
JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA', 'USADA')
GROUP BY p.id_pelicula, p.titulo
HAVING COUNT(e.id_entrada) > (
    SELECT COUNT(*) * 1.0 / (SELECT COUNT(*) FROM peliculas)
    FROM entradas
    WHERE estado IN ('PAGADA', 'USADA')
)
ORDER BY entradas_validas DESC, pelicula ASC;

-- C20
SELECT es.nombre AS espectador, COUNT(DISTINCT sa.id_sede) AS sedes_distintas
FROM espectadores es
JOIN entradas e ON e.id_espectador = es.id_espectador AND e.estado IN ('PAGADA', 'USADA')
JOIN sesiones s ON s.id_sesion = e.id_sesion
JOIN salas sa ON sa.id_sala = s.id_sala
GROUP BY es.id_espectador, es.nombre
HAVING COUNT(DISTINCT sa.id_sede) >= 2
ORDER BY sedes_distintas DESC, espectador ASC;

-- C21
SELECT p.titulo AS pelicula, COUNT(e.id_entrada) AS entradas_validas, ROUND(SUM(s.precio), 2) AS recaudacion
FROM peliculas p
JOIN sesiones s ON s.id_pelicula = p.id_pelicula
JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA', 'USADA')
GROUP BY p.id_pelicula, p.titulo
ORDER BY recaudacion DESC, pelicula ASC
LIMIT 3;

-- C22
SELECT se.nombre AS sede,
       ROUND(COALESCE(SUM(CASE WHEN e.id_entrada IS NULL THEN 0 ELSE s.precio END), 0), 2) AS recaudacion
FROM sedes se
LEFT JOIN salas sa ON sa.id_sede = se.id_sede
LEFT JOIN sesiones s ON s.id_sala = sa.id_sala
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA', 'USADA')
GROUP BY se.id_sede, se.nombre
ORDER BY recaudacion DESC, sede ASC;

-- C23
SELECT e.fecha_compra, COUNT(e.id_entrada) AS entradas_validas, ROUND(SUM(s.precio), 2) AS total
FROM entradas e
JOIN sesiones s ON s.id_sesion = e.id_sesion
WHERE e.estado IN ('PAGADA', 'USADA')
GROUP BY e.fecha_compra
ORDER BY e.fecha_compra ASC;

-- C24
SELECT p.titulo AS pelicula, s.fecha AS fecha_sesion, e.estado AS estado_entrada, s.precio
FROM entradas e
JOIN sesiones s ON s.id_sesion = e.id_sesion
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
WHERE e.id_espectador = 1
ORDER BY fecha_sesion ASC, pelicula ASC;

-- C25
SELECT s.fecha,
       COUNT(DISTINCT CASE WHEN s.estado = 'PROGRAMADA' THEN s.id_sesion END) AS sesiones_programadas,
       COUNT(e.id_entrada) AS entradas_validas,
       ROUND(COALESCE(SUM(CASE WHEN e.id_entrada IS NULL THEN 0 ELSE s.precio END), 0), 2) AS recaudacion
FROM sesiones s
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA', 'USADA')
GROUP BY s.fecha
ORDER BY s.fecha ASC;

-- C26
SELECT p.titulo AS pelicula, COUNT(v.id_valoracion) AS numero_valoraciones, ROUND(AVG(v.puntuacion), 2) AS media
FROM peliculas p
LEFT JOIN valoraciones v ON v.id_pelicula = p.id_pelicula
GROUP BY p.id_pelicula, p.titulo
ORDER BY pelicula ASC;

-- C27
SELECT p.genero,
       COUNT(DISTINCT p.id_pelicula) AS titulos,
       COUNT(DISTINCT s.id_sesion) AS sesiones,
       COUNT(DISTINCT e.id_entrada) AS entradas_validas
FROM peliculas p
LEFT JOIN sesiones s ON s.id_pelicula = p.id_pelicula
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA', 'USADA')
GROUP BY p.genero
ORDER BY p.genero ASC;

-- C28
SELECT se.nombre AS sede, sa.nombre AS sala
FROM salas sa
JOIN sedes se ON se.id_sede = sa.id_sede
WHERE NOT EXISTS (SELECT 1 FROM sesiones s WHERE s.id_sala = sa.id_sala AND s.estado = 'PROGRAMADA')
ORDER BY sede ASC, sala ASC;

-- C29: venta atómica de una entrada (id_sesion = 1, id_espectador = 8, fecha_compra = 2026-11-10).
-- Ejecutar sobre una copia de la base. Todo ocurre en una única transacción con bloqueo de escritura.
-- Si alguna comprobación devuelve 0 filas o 0, se hace ROLLBACK y el resultado es 0.
PRAGMA foreign_keys = ON;
BEGIN IMMEDIATE;

-- 1. La sesión existe y está PROGRAMADA (debe devolver 1).
SELECT COUNT(*) AS sesion_programada FROM sesiones WHERE id_sesion = 1 AND estado = 'PROGRAMADA';

-- 2. El espectador existe y está activo (debe devolver 1).
SELECT COUNT(*) AS espectador_activo FROM espectadores WHERE id_espectador = 8 AND activo = 1;

-- 3. No tiene ya entrada en esa sesión, aunque esté cancelada (debe devolver 0).
SELECT COUNT(*) AS entrada_previa FROM entradas WHERE id_sesion = 1 AND id_espectador = 8;

-- 4. Quedan plazas: aforo - entradas válidas > 0 (debe devolver 1 en la base inicial).
SELECT sa.aforo - (SELECT COUNT(*) FROM entradas e WHERE e.id_sesion = 1 AND e.estado IN ('PAGADA', 'USADA')) AS plazas_libres
FROM sesiones s JOIN salas sa ON sa.id_sala = s.id_sala
WHERE s.id_sesion = 1;

-- 5. Inserción condicionada a todas las reglas anteriores dentro de la misma transacción.
INSERT INTO entradas (id_sesion, id_espectador, fecha_compra, estado)
SELECT 1, 8, '2026-11-10', 'PAGADA'
WHERE EXISTS (SELECT 1 FROM sesiones WHERE id_sesion = 1 AND estado = 'PROGRAMADA')
  AND EXISTS (SELECT 1 FROM espectadores WHERE id_espectador = 8 AND activo = 1)
  AND NOT EXISTS (SELECT 1 FROM entradas WHERE id_sesion = 1 AND id_espectador = 8)
  AND (SELECT sa.aforo FROM sesiones s JOIN salas sa ON sa.id_sala = s.id_sala WHERE s.id_sesion = 1)
      > (SELECT COUNT(*) FROM entradas WHERE id_sesion = 1 AND estado IN ('PAGADA', 'USADA'));

-- 6. Resultado: 1 si se insertó la fila (y su id_entrada); si changes() = 0, ROLLBACK.
SELECT changes() AS resultado, CASE WHEN changes() = 1 THEN last_insert_rowid() END AS id_entrada;
COMMIT;

-- C30: cancelación de la entrada 1 y liberación de plaza (sobre una copia fresca de la base inicial).
-- Una única transacción; no se borra la entrada, solo cambia su estado.
BEGIN IMMEDIATE;

-- 1. La entrada existe y no está cancelada (debe devolver 1).
SELECT COUNT(*) AS cancelable FROM entradas WHERE id_entrada = 1 AND estado <> 'CANCELADA';

-- 2. Cambio de estado. Si changes() = 0 (no existe o ya cancelada), ROLLBACK y resultado 0.
UPDATE entradas SET estado = 'CANCELADA' WHERE id_entrada = 1 AND estado <> 'CANCELADA';
SELECT changes() AS resultado;
COMMIT;

-- 3. Comprobación: estado final y plazas libres de la sesión 1 (CANCELADA y 2 en la copia fresca).
SELECT e.estado AS estado_final,
       sa.aforo - (SELECT COUNT(*) FROM entradas x WHERE x.id_sesion = e.id_sesion AND x.estado IN ('PAGADA', 'USADA')) AS plazas_libres
FROM entradas e
JOIN sesiones s ON s.id_sesion = e.id_sesion
JOIN salas sa ON sa.id_sala = s.id_sala
WHERE e.id_entrada = 1;
