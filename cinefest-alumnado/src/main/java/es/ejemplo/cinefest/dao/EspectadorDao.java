package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Espectador;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EspectadorEntity;
import es.ejemplo.cinefest.orm.mapper.EspectadorMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EspectadorDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(EspectadorDao.class.getName());

    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea el DAO con su propia factoría a partir de la URL JDBC.
     *
     * @param jdbcUrl URL de la base de datos SQLite
     */
    public EspectadorDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    /**
     * Crea el DAO usando una factoría externa, que no se cierra al cerrar el DAO.
     *
     * @param factory factoría compartida
     */
    public EspectadorDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Espectador dato) {
        return dato != null && dato.getNombre() != null && !dato.getNombre().isBlank()
                && dato.getEmail() != null && !dato.getEmail().isBlank()
                && dato.getFechaAlta() != null
                && dato.getActivo() != null && (dato.getActivo() == 0 || dato.getActivo() == 1);
    }

    /**
     * Lista todos los espectadores ordenados por id.
     *
     * @return espectadores; lista vacía si no hay datos o hay error
     */
    public List<Espectador> listar() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT es FROM EspectadorEntity es ORDER BY es.idEspectador",
                            EspectadorEntity.class)
                    .getResultStream().map(EspectadorMapper::desdeEntidad).toList();
        } catch (RuntimeException e) {
            registrar("listar espectadores", e);
            return List.of();
        }
    }

    /**
     * Busca un espectador por su id.
     *
     * @param id id del espectador
     * @return el espectador, o vacío si no existe, el id no es válido o hay error
     */
    public Optional<Espectador> buscarPorId(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(EspectadorEntity.class, id)).map(EspectadorMapper::desdeEntidad);
        } catch (RuntimeException e) {
            registrar("buscar espectador por id", e);
            return Optional.empty();
        }
    }

    /**
     * Inserta un espectador.
     *
     * @param dato espectador con nombre, email, fecha de alta y activo (0/1)
     * @return id generado, o 0 si los datos no son válidos o falla la BD (email duplicado)
     */
    public int insertar(Espectador dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                EspectadorEntity entidad = EspectadorMapper.aEntidad(dato);
                entidad.setIdEspectador(null);
                entityManager.persist(entidad);
                tx.commit();
                dato.setIdEspectador(entidad.getIdEspectador());
                return entidad.getIdEspectador();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("insertar espectador", e);
                return 0;
            }
        }
    }

    /**
     * Actualiza un espectador existente.
     *
     * @param dato espectador con id y datos nuevos
     * @return 1 si se actualiza, 0 si no existe, los datos no son válidos o falla la BD
     */
    public int actualizar(Espectador dato) {
        if (!validar(dato) || dato.getIdEspectador() == null) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                EspectadorEntity espectador = entityManager.find(EspectadorEntity.class, dato.getIdEspectador());
                if (espectador == null) {
                    tx.rollback();
                    return 0;
                }
                espectador.setNombre(dato.getNombre());
                espectador.setEmail(dato.getEmail());
                espectador.setFechaAlta(dato.getFechaAlta());
                espectador.setActivo(dato.getActivo());
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("actualizar espectador", e);
                return 0;
            }
        }
    }

    /**
     * Elimina un espectador.
     *
     * @param id id del espectador
     * @return 1 si se elimina, 0 si no existe o tiene entradas o valoraciones
     */
    public int eliminar(int id) {
        if (id <= 0) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                EspectadorEntity espectador = entityManager.find(EspectadorEntity.class, id);
                if (espectador == null) {
                    tx.rollback();
                    return 0;
                }
                entityManager.remove(espectador);
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("eliminar espectador", e);
                return 0;
            }
        }
    }

    /**
     * Busca un espectador por su email.
     *
     * @param email email exacto
     * @return el espectador, o vacío si no existe o hay error
     */
    public Optional<Espectador> buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT es FROM EspectadorEntity es WHERE es.email = ?1", EspectadorEntity.class)
                    .setParameter(1, email)
                    .getResultStream().findFirst().map(EspectadorMapper::desdeEntidad);
        } catch (RuntimeException e) {
            registrar("buscar espectador por email", e);
            return Optional.empty();
        }
    }

    /**
     * Espectadores que nunca compraron entradas (C09).
     *
     * @return espectadores ordenados por nombre; vacía si no hay o hay error
     */
    public List<Espectador> espectadoresSinEntradas() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT es FROM EspectadorEntity es WHERE NOT EXISTS "
                            + "(SELECT e FROM EntradaEntity e WHERE e.idEspectador = es.idEspectador) ORDER BY es.nombre",
                            EspectadorEntity.class)
                    .getResultStream().map(EspectadorMapper::desdeEntidad).toList();
        } catch (RuntimeException e) {
            registrar("espectadores sin entradas", e);
            return List.of();
        }
    }

    /**
     * Ranking de espectadores por entradas no canceladas, incluidos los que tienen cero (C13).
     *
     * @return filas {espectador, total_entradas} ordenadas por total desc y nombre; vacía si hay error
     */
    public List<Object[]> rankingEspectadores() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT es.nombre, COUNT(e) AS total FROM EspectadorEntity es "
                            + "LEFT JOIN EntradaEntity e ON e.idEspectador = es.idEspectador AND e.estado IN ('PAGADA', 'USADA') "
                            + "GROUP BY es.idEspectador, es.nombre ORDER BY total DESC, es.nombre",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("ranking espectadores", e);
            return List.of();
        }
    }

    /**
     * Espectadores con entradas válidas en un mínimo de sedes distintas (C20).
     *
     * @param minimo número mínimo de sedes
     * @return filas {espectador, sedes_distintas} ordenadas por sedes desc y nombre; vacía si hay error
     */
    public List<Object[]> espectadoresVariasSedes(int minimo) {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT es.nombre, COUNT(DISTINCT sa.idSede) AS sedes FROM EspectadorEntity es, EntradaEntity e, "
                            + "SesionEntity s, SalaEntity sa WHERE e.idEspectador = es.idEspectador AND e.idSesion = s.idSesion "
                            + "AND s.idSala = sa.idSala AND e.estado IN ('PAGADA', 'USADA') "
                            + "GROUP BY es.idEspectador, es.nombre HAVING COUNT(DISTINCT sa.idSede) >= ?1 "
                            + "ORDER BY sedes DESC, es.nombre",
                            Object[].class)
                    .setParameter(1, (long) minimo)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("espectadores varias sedes", e);
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
