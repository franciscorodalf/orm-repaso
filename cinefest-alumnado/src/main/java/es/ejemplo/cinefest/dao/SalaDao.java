package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Sala;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.SalaEntity;
import es.ejemplo.cinefest.orm.mapper.SalaMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SalaDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(SalaDao.class.getName());

    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea el DAO con su propia factoría a partir de la URL JDBC.
     *
     * @param jdbcUrl URL de la base de datos SQLite
     */
    public SalaDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    /**
     * Crea el DAO usando una factoría externa, que no se cierra al cerrar el DAO.
     *
     * @param factory factoría compartida
     */
    public SalaDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Sala dato) {
        return dato != null && dato.getNombre() != null && !dato.getNombre().isBlank()
                && dato.getAforo() != null && dato.getAforo() > 0 && dato.getIdSede() != null;
    }

    /**
     * Lista todas las salas ordenadas por id.
     *
     * @return salas; lista vacía si no hay datos o hay error
     */
    public List<Sala> listar() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT sa FROM SalaEntity sa ORDER BY sa.idSala",
                            SalaEntity.class)
                    .getResultStream().map(SalaMapper::desdeEntidad).toList();
        } catch (RuntimeException e) {
            registrar("listar salas", e);
            return List.of();
        }
    }

    /**
     * Busca una sala por su id.
     *
     * @param id id de la sala
     * @return la sala, o vacío si no existe, el id no es válido o hay error
     */
    public Optional<Sala> buscarPorId(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(SalaEntity.class, id)).map(SalaMapper::desdeEntidad);
        } catch (RuntimeException e) {
            registrar("buscar sala por id", e);
            return Optional.empty();
        }
    }

    /**
     * Inserta una sala.
     *
     * @param dato sala con nombre, aforo positivo y sede
     * @return id generado, o 0 si los datos no son válidos o falla la BD (sede inexistente, nombre repetido en la sede)
     */
    public int insertar(Sala dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                SalaEntity entidad = SalaMapper.aEntidad(dato);
                entidad.setIdSala(null);
                entityManager.persist(entidad);
                tx.commit();
                dato.setIdSala(entidad.getIdSala());
                return entidad.getIdSala();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("insertar sala", e);
                return 0;
            }
        }
    }

    /**
     * Actualiza una sala existente.
     *
     * @param dato sala con id y datos nuevos
     * @return 1 si se actualiza, 0 si no existe, los datos no son válidos o falla la BD
     */
    public int actualizar(Sala dato) {
        if (!validar(dato) || dato.getIdSala() == null) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                SalaEntity sala = entityManager.find(SalaEntity.class, dato.getIdSala());
                if (sala == null) {
                    tx.rollback();
                    return 0;
                }
                sala.setNombre(dato.getNombre());
                sala.setAforo(dato.getAforo());
                sala.setIdSede(dato.getIdSede());
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("actualizar sala", e);
                return 0;
            }
        }
    }

    /**
     * Elimina una sala.
     *
     * @param id id de la sala
     * @return 1 si se elimina, 0 si no existe o tiene sesiones asociadas
     */
    public int eliminar(int id) {
        if (id <= 0) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                SalaEntity sala = entityManager.find(SalaEntity.class, id);
                if (sala == null) {
                    tx.rollback();
                    return 0;
                }
                entityManager.remove(sala);
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("eliminar sala", e);
                return 0;
            }
        }
    }

    /**
     * Salas con el nombre de su sede (C06).
     *
     * @return filas {sede, sala, aforo} ordenadas por sede y sala; vacía si hay error
     */
    public List<Object[]> salasConSede() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT se.nombre, sa.nombre, sa.aforo FROM SalaEntity sa, SedeEntity se "
                            + "WHERE sa.idSede = se.idSede ORDER BY se.nombre, sa.nombre",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("salas con sede", e);
            return List.of();
        }
    }

    /**
     * Salas sin ninguna sesión programada (C28).
     *
     * @return salas ordenadas por sede y nombre; vacía si no hay o hay error
     */
    public List<Sala> salasSinSesiones() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT sa FROM SalaEntity sa, SedeEntity se WHERE sa.idSede = se.idSede "
                            + "AND NOT EXISTS (SELECT s FROM SesionEntity s WHERE s.idSala = sa.idSala AND s.estado = 'PROGRAMADA') "
                            + "ORDER BY se.nombre, sa.nombre",
                            SalaEntity.class)
                    .getResultStream().map(SalaMapper::desdeEntidad).toList();
        } catch (RuntimeException e) {
            registrar("salas sin sesiones", e);
            return List.of();
        }
    }

    /**
     * Ocupación de cada sala: sesiones y entradas válidas (PAGADA o USADA).
     *
     * @return filas {sala, aforo, sesiones, entradas_validas} ordenadas por sala; vacía si hay error
     */
    public List<Object[]> ocupacionPorSala() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT sa.nombre, sa.aforo, COUNT(DISTINCT s.idSesion), COUNT(e.idEntrada) FROM SalaEntity sa "
                            + "LEFT JOIN SesionEntity s ON s.idSala = sa.idSala "
                            + "LEFT JOIN EntradaEntity e ON e.idSesion = s.idSesion AND e.estado IN ('PAGADA', 'USADA') "
                            + "GROUP BY sa.idSala, sa.nombre, sa.aforo ORDER BY sa.nombre",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("ocupacion por sala", e);
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
