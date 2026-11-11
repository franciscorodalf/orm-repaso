INSERT INTO sedes VALUES (1,'Auditorio Atlántico','Las Palmas'),(2,'Centro Cultural Sur','Maspalomas'),(3,'Cine del Puerto','Arrecife');
INSERT INTO salas VALUES (1,'Sala Azul',5,1),(2,'Sala Roja',8,1),(3,'Sala Dunas',4,2),(4,'Sala Volcán',6,3);
INSERT INTO peliculas VALUES
 (1,'Mareas','Drama',100,'España',2024),(2,'Órbita','Ciencia ficción',115,'Francia',2025),
 (3,'Horizonte','Drama',95,'España',2023),(4,'Ecos','Documental',80,'Portugal',2025),
 (5,'Aurora','Animación',88,'España',2026),(6,'Nómadas','Documental',105,'Marruecos',2024),
 (7,'Bruma','Drama',110,'Chile',2022),(8,'Luz de Sal','Comedia',92,'España',2025),
 (9,'Islas Invisibles','Documental',75,'España',2026);
INSERT INTO sesiones VALUES
 (1,1,1,'2026-11-12','18:00',6,'PROGRAMADA'),
 (2,2,2,'2026-11-12','20:00',8,'PROGRAMADA'),
 (3,3,3,'2026-11-13','17:00',5,'PROGRAMADA'),
 (4,4,4,'2026-11-13','19:00',6,'PROGRAMADA'),
 (5,1,3,'2026-11-14','18:00',7,'PROGRAMADA'),
 (6,5,1,'2026-11-14','16:00',4,'PROGRAMADA'),
 (7,6,2,'2026-11-15','19:00',6,'PROGRAMADA'),
 (8,2,4,'2026-11-15','20:00',8,'CANCELADA'),
 (9,7,1,'2026-11-16','18:00',5,'PROGRAMADA'),
 (10,8,3,'2026-11-16','20:00',5,'PROGRAMADA'),
 (11,3,2,'2026-11-17','18:00',7,'PROGRAMADA'),
 (12,4,4,'2026-11-17','20:00',6,'PROGRAMADA');
INSERT INTO espectadores VALUES
 (1,'Ana','ana@example.org','2026-10-01',1),
 (2,'Bruno','bruno@example.org','2026-10-02',1),
 (3,'Carla','carla@example.org','2026-10-03',1),
 (4,'Diego','diego@example.org','2026-10-04',1),
 (5,'Elena','elena@example.org','2026-10-05',1),
 (6,'Fátima','fatima@example.org','2026-10-06',1),
 (7,'Gabriel','gabriel@example.org','2026-10-07',0),
 (8,'Hugo','hugo@example.org','2026-10-08',1);
INSERT INTO entradas VALUES
 (1,1,1,'2026-11-01','PAGADA'),(2,1,2,'2026-11-01','PAGADA'),
 (3,1,3,'2026-11-02','PAGADA'),(4,1,4,'2026-11-03','PAGADA'),
 (5,1,5,'2026-11-03','CANCELADA'),(6,2,1,'2026-11-02','PAGADA'),
 (7,2,2,'2026-11-02','PAGADA'),(8,2,3,'2026-11-03','PAGADA'),
 (9,3,1,'2026-11-04','PAGADA'),(10,3,4,'2026-11-04','PAGADA'),
 (11,4,2,'2026-11-04','PAGADA'),(12,5,3,'2026-11-05','PAGADA'),
 (13,5,4,'2026-11-05','PAGADA'),(14,5,5,'2026-11-05','PAGADA'),
 (15,6,6,'2026-11-06','PAGADA'),(16,6,1,'2026-11-06','PAGADA'),
 (17,7,5,'2026-11-07','PAGADA'),(18,10,2,'2026-11-07','PAGADA'),
 (19,11,3,'2026-11-08','PAGADA'),(20,2,5,'2026-11-08','CANCELADA'),
 (21,3,5,'2026-11-09','PAGADA'),(22,3,6,'2026-11-09','PAGADA');
INSERT INTO valoraciones VALUES
 (1,1,1,5,'Excelente','2026-11-02'),(2,1,2,4,NULL,'2026-11-02'),
 (3,1,3,5,NULL,'2026-11-03'),(4,2,1,4,NULL,'2026-11-03'),
 (5,2,2,3,NULL,'2026-11-03'),(6,3,1,5,NULL,'2026-11-04'),
 (7,3,4,4,NULL,'2026-11-04'),(8,4,2,5,NULL,'2026-11-05'),
 (9,5,3,5,NULL,'2026-11-05'),(10,5,5,4,NULL,'2026-11-06'),
 (11,6,6,3,NULL,'2026-11-06'),(12,7,5,4,NULL,'2026-11-07');
