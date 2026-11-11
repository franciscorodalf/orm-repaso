package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Sede;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.SedeEntity;
import es.ejemplo.cinefest.orm.mapper.SedeMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SedeDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(SedeDao.class.getName());

    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea el DAO con su propia factoría a partir de la URL JDBC.
     *
     * @param jdbcUrl URL de la base de datos SQLite
     */
    public SedeDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    /**
     * Crea el DAO usando una factoría externa, que no se cierra al cerrar el DAO.
     *
     * @param factory factoría compartida
     */
    public SedeDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Sede dato) {
        return dato != null && dato.getNombre() != null && !dato.getNombre().isBlank()
                && dato.getMunicipio() != null && !dato.getMunicipio().isBlank();
    }

    /**
     * Lista todas las sedes ordenadas por id.
     *
     * @return sedes; lista vacía si no hay datos o hay error
     */
    public List<Sede> listar() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT s FROM SedeEntity s ORDER BY s.idSede", SedeEntity.class)
                    .getResultStream().map(SedeMapper::desdeEntidad).toList();
        } catch (RuntimeException e) {
            registrar("listar sedes", e);
            return List.of();
        }
    }

    /**
     * Busca una sede por su id.
     *
     * @param id id de la sede
     * @return la sede, o vacío si no existe, el id no es válido o hay error
     */
    public Optional<Sede> buscarPorId(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(SedeEntity.class, id)).map(SedeMapper::desdeEntidad);
        } catch (RuntimeException e) {
            registrar("buscar sede por id", e);
            return Optional.empty();
        }
    }

    /**
     * Inserta una sede.
     *
     * @param dato sede con nombre y municipio
     * @return id generado, o 0 si los datos no son válidos o falla la BD (nombre duplicado)
     */
    public int insertar(Sede dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                SedeEntity entidad = SedeMapper.aEntidad(dato);
                entidad.setIdSede(null);
                entityManager.persist(entidad);
                tx.commit();
                dato.setIdSede(entidad.getIdSede());
                return entidad.getIdSede();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("insertar sede", e);
                return 0;
            }
        }
    }

    /**
     * Actualiza una sede existente.
     *
     * @param dato sede con id y datos nuevos
     * @return 1 si se actualiza, 0 si no existe, los datos no son válidos o falla la BD
     */
    public int actualizar(Sede dato) {
        if (!validar(dato) || dato.getIdSede() == null) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                SedeEntity sede = entityManager.find(SedeEntity.class, dato.getIdSede());
                if (sede == null) {
                    tx.rollback();
                    return 0;
                }
                sede.setNombre(dato.getNombre());
                sede.setMunicipio(dato.getMunicipio());
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("actualizar sede", e);
                return 0;
            }
        }
    }

    /**
     * Elimina una sede.
     *
     * @param id id de la sede
     * @return 1 si se elimina, 0 si no existe o tiene salas asociadas
     */
    public int eliminar(int id) {
        if (id <= 0) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                SedeEntity sede = entityManager.find(SedeEntity.class, id);
                if (sede == null) {
                    tx.rollback();
                    return 0;
                }
                entityManager.remove(sede);
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("eliminar sede", e);
                return 0;
            }
        }
    }

    /**
     * Salas de una sede.
     *
     * @param idSede id de la sede
     * @return filas {sala, aforo} ordenadas por sala; vacía si no hay o hay error
     */
    public List<Object[]> salasPorSede(int idSede) {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT sa.nombre, sa.aforo FROM SalaEntity sa WHERE sa.idSede = ?1 ORDER BY sa.nombre",
                            Object[].class)
                    .setParameter(1, idSede)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("salas por sede", e);
            return List.of();
        }
    }

    /**
     * Número de salas y aforo total por sede, incluidas sedes sin salas (C12).
     *
     * @return filas {sede, total_salas, aforo_total} ordenadas por sede; vacía si hay error
     */
    public List<Object[]> aforoTotalPorSede() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT se.nombre, COUNT(sa), COALESCE(SUM(sa.aforo), 0) FROM SedeEntity se "
                            + "LEFT JOIN SalaEntity sa ON sa.idSede = se.idSede "
                            + "GROUP BY se.idSede, se.nombre ORDER BY se.nombre",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("aforo total por sede", e);
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
