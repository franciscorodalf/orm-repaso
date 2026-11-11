package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Pelicula;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.PeliculaEntity;
import es.ejemplo.cinefest.orm.mapper.PeliculaMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeliculaDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(PeliculaDao.class.getName());

    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea el DAO con su propia factoría a partir de la URL JDBC.
     *
     * @param jdbcUrl URL de la base de datos SQLite
     */
    public PeliculaDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    /**
     * Crea el DAO usando una factoría externa, que no se cierra al cerrar el DAO.
     *
     * @param factory factoría compartida
     */
    public PeliculaDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Pelicula dato) {
        return dato != null && dato.getTitulo() != null && !dato.getTitulo().isBlank()
                && dato.getGenero() != null && !dato.getGenero().isBlank()
                && dato.getPais() != null && !dato.getPais().isBlank()
                && dato.getDuracionMin() != null && dato.getDuracionMin() > 0
                && dato.getAnio() != null;
    }

    /**
     * Lista todas las películas ordenadas por id.
     *
     * @return películas; lista vacía si no hay datos o hay error
     */
    public List<Pelicula> listar() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT p FROM PeliculaEntity p ORDER BY p.idPelicula",
                            PeliculaEntity.class)
                    .getResultStream().map(PeliculaMapper::desdeEntidad).toList();
        } catch (RuntimeException e) {
            registrar("listar peliculas", e);
            return List.of();
        }
    }

    /**
     * Busca una película por su id.
     *
     * @param id id de la película
     * @return la película, o vacío si no existe, el id no es válido o hay error
     */
    public Optional<Pelicula> buscarPorId(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(PeliculaEntity.class, id)).map(PeliculaMapper::desdeEntidad);
        } catch (RuntimeException e) {
            registrar("buscar pelicula por id", e);
            return Optional.empty();
        }
    }

    /**
     * Inserta una película.
     *
     * @param dato película con título, género, país, duración positiva y año
     * @return id generado, o 0 si los datos no son válidos o falla la BD
     */
    public int insertar(Pelicula dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                PeliculaEntity entidad = PeliculaMapper.aEntidad(dato);
                entidad.setIdPelicula(null);
                entityManager.persist(entidad);
                tx.commit();
                dato.setIdPelicula(entidad.getIdPelicula());
                return entidad.getIdPelicula();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("insertar pelicula", e);
                return 0;
            }
        }
    }

    /**
     * Actualiza una película existente.
     *
     * @param dato película con id y datos nuevos
     * @return 1 si se actualiza, 0 si no existe, los datos no son válidos o falla la BD
     */
    public int actualizar(Pelicula dato) {
        if (!validar(dato) || dato.getIdPelicula() == null) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                PeliculaEntity pelicula = entityManager.find(PeliculaEntity.class, dato.getIdPelicula());
                if (pelicula == null) {
                    tx.rollback();
                    return 0;
                }
                pelicula.setTitulo(dato.getTitulo());
                pelicula.setGenero(dato.getGenero());
                pelicula.setDuracionMin(dato.getDuracionMin());
                pelicula.setPais(dato.getPais());
                pelicula.setAnio(dato.getAnio());
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("actualizar pelicula", e);
                return 0;
            }
        }
    }

    /**
     * Elimina una película.
     *
     * @param id id de la película
     * @return 1 si se elimina, 0 si no existe o tiene sesiones o valoraciones
     */
    public int eliminar(int id) {
        if (id <= 0) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                PeliculaEntity pelicula = entityManager.find(PeliculaEntity.class, id);
                if (pelicula == null) {
                    tx.rollback();
                    return 0;
                }
                entityManager.remove(pelicula);
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("eliminar pelicula", e);
                return 0;
            }
        }
    }

    /**
     * Películas de un género (C03).
     *
     * @param genero género exacto
     * @return películas ordenadas por título; vacía si no hay o hay error
     */
    public List<Pelicula> buscarPorGenero(String genero) {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT p FROM PeliculaEntity p WHERE p.genero = ?1 ORDER BY p.titulo",
                            PeliculaEntity.class)
                    .setParameter(1, genero)
                    .getResultStream().map(PeliculaMapper::desdeEntidad).toList();
        } catch (RuntimeException e) {
            registrar("buscar por genero", e);
            return List.of();
        }
    }

    /**
     * Películas estrenadas desde un año, incluido.
     *
     * @param anio año mínimo
     * @return películas ordenadas por año y título; vacía si no hay o hay error
     */
    public List<Pelicula> peliculasDesdeAnio(int anio) {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT p FROM PeliculaEntity p WHERE p.anio >= ?1 ORDER BY p.anio, p.titulo",
                            PeliculaEntity.class)
                    .setParameter(1, anio)
                    .getResultStream().map(PeliculaMapper::desdeEntidad).toList();
        } catch (RuntimeException e) {
            registrar("peliculas desde anio", e);
            return List.of();
        }
    }

    /**
     * Películas sin ninguna sesión (C16).
     *
     * @return películas ordenadas por título; vacía si no hay o hay error
     */
    public List<Pelicula> peliculasSinSesiones() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT p FROM PeliculaEntity p WHERE NOT EXISTS "
                            + "(SELECT s FROM SesionEntity s WHERE s.idPelicula = p.idPelicula) ORDER BY p.titulo",
                            PeliculaEntity.class)
                    .getResultStream().map(PeliculaMapper::desdeEntidad).toList();
        } catch (RuntimeException e) {
            registrar("peliculas sin sesiones", e);
            return List.of();
        }
    }

    /**
     * Número de valoraciones y media por película, incluidas las no valoradas (C26).
     *
     * @return filas {pelicula, numero_valoraciones, media} ordenadas por película; media null si no hay valoraciones
     */
    public List<Object[]> peliculasConMediaValoraciones() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT p.titulo, COUNT(v), ROUND(AVG(v.puntuacion), 2) FROM PeliculaEntity p "
                            + "LEFT JOIN ValoracionEntity v ON v.idPelicula = p.idPelicula "
                            + "GROUP BY p.idPelicula, p.titulo ORDER BY p.titulo",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("peliculas con media", e);
            return List.of();
        }
    }

    /**
     * Películas con un mínimo de valoraciones y su media (C14).
     *
     * @param minimo número mínimo de valoraciones
     * @return filas {pelicula, numero_valoraciones, media} ordenadas por media desc y película; vacía si hay error
     */
    public List<Object[]> mejorValoradas(int minimo) {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT p.titulo, COUNT(v), ROUND(AVG(v.puntuacion), 2) AS media FROM PeliculaEntity p, ValoracionEntity v "
                            + "WHERE v.idPelicula = p.idPelicula GROUP BY p.idPelicula, p.titulo "
                            + "HAVING COUNT(v) >= ?1 ORDER BY media DESC, p.titulo",
                            Object[].class)
                    .setParameter(1, (long) minimo)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("mejor valoradas", e);
            return List.of();
        }
    }

    /**
     * Películas con más entradas válidas que la media de todas las películas (C19).
     *
     * @return filas {pelicula, entradas_validas} ordenadas por entradas desc y película; vacía si hay error
     */
    public List<Object[]> masVendidasQueMedia() {
        try (var entityManager = managerFactory.createEntityManager()) {
            Long validas = entityManager.createQuery(
                    "SELECT COUNT(e) FROM EntradaEntity e WHERE e.estado IN ('PAGADA', 'USADA')", Long.class)
                    .getSingleResult();
            Long peliculas = entityManager.createQuery("SELECT COUNT(p) FROM PeliculaEntity p", Long.class)
                    .getSingleResult();
            if (peliculas == 0) {
                return List.of();
            }
            double media = (double) validas / peliculas;
            return entityManager.createQuery(
                    "SELECT p.titulo, COUNT(e) AS entradas FROM PeliculaEntity p, SesionEntity s, EntradaEntity e "
                            + "WHERE s.idPelicula = p.idPelicula AND e.idSesion = s.idSesion AND e.estado IN ('PAGADA', 'USADA') "
                            + "GROUP BY p.idPelicula, p.titulo HAVING COUNT(e) * 1.0 > ?1 ORDER BY entradas DESC, p.titulo",
                    Object[].class)
                    .setParameter(1, media)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("mas vendidas que media", e);
            return List.of();
        }
    }

    /**
     * Títulos, sesiones y entradas válidas por género (C27).
     *
     * @return filas {genero, titulos, sesiones, entradas_validas} ordenadas por género; vacía si hay error
     */
    public List<Object[]> estadisticasGenero() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT p.genero, COUNT(DISTINCT p.idPelicula), COUNT(DISTINCT s.idSesion), COUNT(DISTINCT e.idEntrada) "
                            + "FROM PeliculaEntity p "
                            + "LEFT JOIN SesionEntity s ON s.idPelicula = p.idPelicula "
                            + "LEFT JOIN EntradaEntity e ON e.idSesion = s.idSesion AND e.estado IN ('PAGADA', 'USADA') "
                            + "GROUP BY p.genero ORDER BY p.genero",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("estadisticas genero", e);
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
