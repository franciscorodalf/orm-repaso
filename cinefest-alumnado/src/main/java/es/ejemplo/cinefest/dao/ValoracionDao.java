package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Valoracion;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.ValoracionEntity;
import es.ejemplo.cinefest.orm.mapper.ValoracionMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ValoracionDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(ValoracionDao.class.getName());

    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea el DAO con su propia factoría a partir de la URL JDBC.
     *
     * @param jdbcUrl URL de la base de datos SQLite
     */
    public ValoracionDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    /**
     * Crea el DAO usando una factoría externa, que no se cierra al cerrar el DAO.
     *
     * @param factory factoría compartida
     */
    public ValoracionDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Valoracion dato) {
        return dato != null && dato.getIdPelicula() != null && dato.getIdEspectador() != null
                && dato.getPuntuacion() != null && dato.getPuntuacion() >= 1 && dato.getPuntuacion() <= 5
                && dato.getFecha() != null;
    }

    /**
     * Lista todas las valoraciones ordenadas por id.
     *
     * @return valoraciones; lista vacía si no hay datos o hay error
     */
    public List<Valoracion> listar() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT v FROM ValoracionEntity v ORDER BY v.idValoracion", ValoracionEntity.class)
                    .getResultStream().map(ValoracionMapper::desdeEntidad).toList();
        } catch (RuntimeException e) {
            registrar("listar valoraciones", e);
            return List.of();
        }
    }

    /**
     * Busca una valoración por su id.
     *
     * @param id id de la valoración
     * @return la valoración, o vacío si no existe, el id no es válido o hay error
     */
    public Optional<Valoracion> buscarPorId(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(ValoracionEntity.class, id)).map(ValoracionMapper::desdeEntidad);
        } catch (RuntimeException e) {
            registrar("buscar valoracion por id", e);
            return Optional.empty();
        }
    }

    /**
     * Inserta una valoración.
     *
     * @param dato valoración con película, espectador, puntuación 1-5 y fecha
     * @return id generado, o 0 si los datos no son válidos o falla la BD (FK inexistente, valoración repetida)
     */
    public int insertar(Valoracion dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                ValoracionEntity entidad = ValoracionMapper.aEntidad(dato);
                entidad.setIdValoracion(null);
                entityManager.persist(entidad);
                tx.commit();
                dato.setIdValoracion(entidad.getIdValoracion());
                return entidad.getIdValoracion();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("insertar valoracion", e);
                return 0;
            }
        }
    }

    /**
     * Actualiza una valoración existente.
     *
     * @param dato valoración con id y datos nuevos
     * @return 1 si se actualiza, 0 si no existe, los datos no son válidos o falla la BD
     */
    public int actualizar(Valoracion dato) {
        if (!validar(dato) || dato.getIdValoracion() == null) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                ValoracionEntity valoracion = entityManager.find(ValoracionEntity.class, dato.getIdValoracion());
                if (valoracion == null) {
                    tx.rollback();
                    return 0;
                }
                valoracion.setIdPelicula(dato.getIdPelicula());
                valoracion.setIdEspectador(dato.getIdEspectador());
                valoracion.setPuntuacion(dato.getPuntuacion());
                valoracion.setComentario(dato.getComentario());
                valoracion.setFecha(dato.getFecha());
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("actualizar valoracion", e);
                return 0;
            }
        }
    }

    /**
     * Elimina una valoración.
     *
     * @param id id de la valoración
     * @return 1 si se elimina, 0 si no existe o hay error
     */
    public int eliminar(int id) {
        if (id <= 0) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                ValoracionEntity valoracion = entityManager.find(ValoracionEntity.class, id);
                if (valoracion == null) {
                    tx.rollback();
                    return 0;
                }
                entityManager.remove(valoracion);
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("eliminar valoracion", e);
                return 0;
            }
        }
    }

    /**
     * Valoraciones con título de película y nombre del espectador (C10).
     *
     * @return filas {pelicula, espectador, puntuacion} ordenadas por película y espectador; vacía si hay error
     */
    public List<Object[]> valoracionesConDetalles() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT p.titulo, es.nombre, v.puntuacion FROM ValoracionEntity v, PeliculaEntity p, EspectadorEntity es "
                            + "WHERE v.idPelicula = p.idPelicula AND v.idEspectador = es.idEspectador "
                            + "ORDER BY p.titulo, es.nombre",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("valoraciones con detalles", e);
            return List.of();
        }
    }

    /**
     * Películas sin ninguna valoración.
     *
     * @return filas {id_pelicula, titulo} ordenadas por título; vacía si no hay o hay error
     */
    public List<Object[]> peliculasSinValoraciones() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT p.idPelicula, p.titulo FROM PeliculaEntity p WHERE NOT EXISTS "
                            + "(SELECT v FROM ValoracionEntity v WHERE v.idPelicula = p.idPelicula) ORDER BY p.titulo",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("peliculas sin valoraciones", e);
            return List.of();
        }
    }

    /**
     * Valoraciones de una película.
     *
     * @param idPelicula id de la película
     * @return filas {espectador, puntuacion, comentario, fecha} ordenadas por fecha y espectador; vacía si no hay o hay error
     */
    public List<Object[]> valoracionesPorPelicula(int idPelicula) {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT es.nombre, v.puntuacion, v.comentario, v.fecha FROM ValoracionEntity v, EspectadorEntity es "
                            + "WHERE v.idEspectador = es.idEspectador AND v.idPelicula = ?1 ORDER BY v.fecha, es.nombre",
                            Object[].class)
                    .setParameter(1, idPelicula)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("valoraciones por pelicula", e);
            return List.of();
        }
    }

    /**
     * Número de valoraciones por puntuación.
     *
     * @return filas {puntuacion, total} ordenadas por puntuación; vacía si hay error
     */
    public List<Object[]> estadisticasPuntuaciones() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT v.puntuacion, COUNT(v) FROM ValoracionEntity v GROUP BY v.puntuacion ORDER BY v.puntuacion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("estadisticas puntuaciones", e);
            return List.of();
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
