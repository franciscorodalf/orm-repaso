package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Sesion;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EntradaEntity;
import es.ejemplo.cinefest.orm.entity.EspectadorEntity;
import es.ejemplo.cinefest.orm.entity.SalaEntity;
import es.ejemplo.cinefest.orm.entity.SesionEntity;
import es.ejemplo.cinefest.orm.mapper.SesionMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SesionDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(SesionDao.class.getName());

    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea el DAO con su propia factoría a partir de la URL JDBC.
     *
     * @param jdbcUrl URL de la base de datos SQLite
     */
    public SesionDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    /**
     * Crea el DAO usando una factoría externa, que no se cierra al cerrar el DAO.
     *
     * @param factory factoría compartida
     */
    public SesionDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Sesion dato) {
        return dato != null && dato.getIdPelicula() != null && dato.getIdSala() != null
                && dato.getFecha() != null && dato.getHora() != null
                && dato.getPrecio() != null && dato.getPrecio() >= 0
                && dato.getEstado() != null
                && List.of("PROGRAMADA", "CANCELADA", "FINALIZADA").contains(dato.getEstado());
    }

    /**
     * Lista todas las sesiones ordenadas por id.
     *
     * @return sesiones; lista vacía si no hay datos o hay error
     */
    public List<Sesion> listar() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT s FROM SesionEntity s ORDER BY s.idSesion", SesionEntity.class)
                    .getResultStream().map(SesionMapper::desdeEntidad).toList();
        } catch (RuntimeException e) {
            registrar("listar sesiones", e);
            return List.of();
        }
    }

    /**
     * Busca una sesión por su id.
     *
     * @param id id de la sesión
     * @return la sesión, o vacío si no existe, el id no es válido o hay error
     */
    public Optional<Sesion> buscarPorId(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(SesionEntity.class, id)).map(SesionMapper::desdeEntidad);
        } catch (RuntimeException e) {
            registrar("buscar sesion por id", e);
            return Optional.empty();
        }
    }

    /**
     * Inserta una sesión.
     *
     * @param dato sesión con película, sala, fecha, hora, precio no negativo y estado válido
     * @return id generado, o 0 si los datos no son válidos o falla la BD (FK inexistente, sala ocupada a esa hora)
     */
    public int insertar(Sesion dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                SesionEntity entidad = SesionMapper.aEntidad(dato);
                entidad.setIdSesion(null);
                entityManager.persist(entidad);
                tx.commit();
                dato.setIdSesion(entidad.getIdSesion());
                return entidad.getIdSesion();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("insertar sesion", e);
                return 0;
            }
        }
    }

    /**
     * Actualiza una sesión existente.
     *
     * @param dato sesión con id y datos nuevos
     * @return 1 si se actualiza, 0 si no existe, los datos no son válidos o falla la BD
     */
    public int actualizar(Sesion dato) {
        if (!validar(dato) || dato.getIdSesion() == null) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                SesionEntity sesion = entityManager.find(SesionEntity.class, dato.getIdSesion());
                if (sesion == null) {
                    tx.rollback();
                    return 0;
                }
                sesion.setIdPelicula(dato.getIdPelicula());
                sesion.setIdSala(dato.getIdSala());
                sesion.setFecha(dato.getFecha());
                sesion.setHora(dato.getHora());
                sesion.setPrecio(dato.getPrecio());
                sesion.setEstado(dato.getEstado());
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("actualizar sesion", e);
                return 0;
            }
        }
    }

    /**
     * Elimina una sesión.
     *
     * @param id id de la sesión
     * @return 1 si se elimina, 0 si no existe o tiene entradas asociadas
     */
    public int eliminar(int id) {
        if (id <= 0) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                SesionEntity sesion = entityManager.find(SesionEntity.class, id);
                if (sesion == null) {
                    tx.rollback();
                    return 0;
                }
                entityManager.remove(sesion);
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("eliminar sesion", e);
                return 0;
            }
        }
    }

    /**
     * Sesiones con película, sede y sala (C07).
     *
     * @return filas {id_sesion, pelicula, sede, sala, fecha, hora} ordenadas por id; vacía si hay error
     */
    public List<Object[]> sesionesConDetalles() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT s.idSesion, p.titulo, se.nombre, sa.nombre, s.fecha, s.hora "
                            + "FROM SesionEntity s, PeliculaEntity p, SalaEntity sa, SedeEntity se "
                            + "WHERE s.idPelicula = p.idPelicula AND s.idSala = sa.idSala AND sa.idSede = se.idSede "
                            + "ORDER BY s.idSesion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("sesiones con detalles", e);
            return List.of();
        }
    }

    /**
     * Sesiones de una fecha concreta (variante de C04 con una sola fecha).
     *
     * @param fecha fecha YYYY-MM-DD
     * @return filas {fecha, hora, id_sesion, precio} ordenadas por hora e id; vacía si no hay o hay error
     */
    public List<Object[]> sesionesPorFecha(String fecha) {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT s.fecha, s.hora, s.idSesion, s.precio FROM SesionEntity s WHERE s.fecha = ?1 "
                            + "ORDER BY s.hora, s.idSesion",
                            Object[].class)
                    .setParameter(1, fecha)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("sesiones por fecha", e);
            return List.of();
        }
    }

    /**
     * Sesiones programadas sin ninguna venta válida (C17).
     *
     * @return filas {id_sesion, pelicula, fecha} ordenadas por id; vacía si no hay o hay error
     */
    public List<Object[]> sesionesSinVentas() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT s.idSesion, p.titulo, s.fecha FROM SesionEntity s, PeliculaEntity p "
                            + "WHERE s.idPelicula = p.idPelicula AND s.estado = 'PROGRAMADA' AND NOT EXISTS "
                            + "(SELECT e FROM EntradaEntity e WHERE e.idSesion = s.idSesion AND e.estado IN ('PAGADA', 'USADA')) "
                            + "ORDER BY s.idSesion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("sesiones sin ventas", e);
            return List.of();
        }
    }

    /**
     * Plazas vendidas (válidas) y libres de cada sesión (C15).
     *
     * @return filas {id_sesion, aforo, vendidas, libres} ordenadas por id; vacía si hay error
     */
    public List<Object[]> disponibilidadSesiones() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT s.idSesion, sa.aforo, COUNT(e), sa.aforo - COUNT(e) FROM SesionEntity s "
                            + "JOIN SalaEntity sa ON sa.idSala = s.idSala "
                            + "LEFT JOIN EntradaEntity e ON e.idSesion = s.idSesion AND e.estado IN ('PAGADA', 'USADA') "
                            + "GROUP BY s.idSesion, sa.aforo ORDER BY s.idSesion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("disponibilidad sesiones", e);
            return List.of();
        }
    }

    /**
     * Sesiones agotadas sin contar cancelaciones (C18).
     *
     * @return filas {id_sesion, pelicula, aforo} ordenadas por id; vacía si no hay o hay error
     */
    public List<Object[]> sesionesAgotadas() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT s.idSesion, p.titulo, sa.aforo FROM SesionEntity s, PeliculaEntity p, SalaEntity sa, EntradaEntity e "
                            + "WHERE s.idPelicula = p.idPelicula AND s.idSala = sa.idSala AND e.idSesion = s.idSesion "
                            + "AND e.estado IN ('PAGADA', 'USADA') "
                            + "GROUP BY s.idSesion, p.titulo, sa.aforo HAVING COUNT(e) >= sa.aforo ORDER BY s.idSesion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("sesiones agotadas", e);
            return List.of();
        }
    }

    /**
     * Top 3 de películas por recaudación de entradas válidas (C21).
     *
     * @return filas {pelicula, entradas_validas, recaudacion} ordenadas por recaudación desc y película; vacía si hay error
     */
    public List<Object[]> rankingRecaudacion() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager.createQuery(
                    "SELECT p.titulo, COUNT(e), SUM(s.precio) AS recaudacion FROM PeliculaEntity p, SesionEntity s, EntradaEntity e "
                            + "WHERE s.idPelicula = p.idPelicula AND e.idSesion = s.idSesion AND e.estado IN ('PAGADA', 'USADA') "
                            + "GROUP BY p.idPelicula, p.titulo ORDER BY recaudacion DESC, p.titulo",
                    Object[].class)
                    .setMaxResults(3)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("ranking recaudacion", e);
            return List.of();
        }
    }

    /**
     * Recaudación por sede, incluidas sedes sin ventas (C22).
     *
     * @return filas {sede, recaudacion} ordenadas por recaudación desc y sede; vacía si hay error
     */
    public List<Object[]> ingresosPorSede() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT se.nombre, SUM(CASE WHEN e.idEntrada IS NULL THEN 0.0 ELSE s.precio END) AS recaudacion "
                            + "FROM SedeEntity se "
                            + "LEFT JOIN SalaEntity sa ON sa.idSede = se.idSede "
                            + "LEFT JOIN SesionEntity s ON s.idSala = sa.idSala "
                            + "LEFT JOIN EntradaEntity e ON e.idSesion = s.idSesion AND e.estado IN ('PAGADA', 'USADA') "
                            + "GROUP BY se.idSede, se.nombre ORDER BY recaudacion DESC, se.nombre",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("ingresos por sede", e);
            return List.of();
        }
    }

    /**
     * Informe por fecha de sesión, incluidos días sin ventas (C25).
     *
     * @return filas {fecha, sesiones_programadas, entradas_validas, recaudacion} ordenadas por fecha; vacía si hay error
     */
    public List<Object[]> informeDiario() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT s.fecha, COUNT(DISTINCT CASE WHEN s.estado = 'PROGRAMADA' THEN s.idSesion END), COUNT(e), "
                            + "SUM(CASE WHEN e.idEntrada IS NULL THEN 0.0 ELSE s.precio END) FROM SesionEntity s "
                            + "LEFT JOIN EntradaEntity e ON e.idSesion = s.idSesion AND e.estado IN ('PAGADA', 'USADA') "
                            + "GROUP BY s.fecha ORDER BY s.fecha",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("informe diario", e);
            return List.of();
        }
    }

    /**
     * Vende una entrada en una única transacción con control de aforo (C29).
     * Bloquea la sesión antes de comprobar plazas para evitar sobreventa.
     *
     * @param idSesion id de una sesión PROGRAMADA
     * @param idEspectador id de un espectador activo sin entrada en esa sesión
     * @param fechaCompra fecha YYYY-MM-DD
     * @return 1 si se vende, 0 si no se cumple alguna regla o hay error (con rollback)
     */
    public int venderEntrada(int idSesion, int idEspectador, String fechaCompra) {
        if (idSesion <= 0 || idEspectador <= 0 || fechaCompra == null) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                int bloqueadas = entityManager
                        .createQuery("UPDATE SesionEntity s SET s.estado = s.estado WHERE s.idSesion = ?1 AND s.estado = 'PROGRAMADA'")
                        .setParameter(1, idSesion)
                        .executeUpdate();
                EspectadorEntity espectador = entityManager.find(EspectadorEntity.class, idEspectador);
                if (bloqueadas == 0 || espectador == null || espectador.getActivo() != 1) {
                    tx.rollback();
                    return 0;
                }
                Long yaTiene = entityManager
                        .createQuery("SELECT COUNT(e) FROM EntradaEntity e WHERE e.idSesion = ?1 AND e.idEspectador = ?2", Long.class)
                        .setParameter(1, idSesion)
                        .setParameter(2, idEspectador)
                        .getSingleResult();
                Long vendidas = entityManager
                        .createQuery("SELECT COUNT(e) FROM EntradaEntity e WHERE e.idSesion = ?1 AND e.estado IN ('PAGADA', 'USADA')", Long.class)
                        .setParameter(1, idSesion)
                        .getSingleResult();
                SesionEntity sesion = entityManager.find(SesionEntity.class, idSesion);
                SalaEntity sala = entityManager.find(SalaEntity.class, sesion.getIdSala());
                if (yaTiene > 0 || vendidas >= sala.getAforo()) {
                    tx.rollback();
                    return 0;
                }
                EntradaEntity entrada = new EntradaEntity();
                entrada.setIdSesion(idSesion);
                entrada.setIdEspectador(idEspectador);
                entrada.setFechaCompra(fechaCompra);
                entrada.setEstado("PAGADA");
                entityManager.persist(entrada);
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("vender entrada", e);
                return 0;
            }
        }
    }

    /**
     * Cierra la factoría si la creó este DAO.
     */
    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen()) {
            managerFactory.close();
        }
    }
}
