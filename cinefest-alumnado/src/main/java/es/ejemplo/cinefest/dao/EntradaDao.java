package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Entrada;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EntradaEntity;
import es.ejemplo.cinefest.orm.mapper.EntradaMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntradaDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(EntradaDao.class.getName());

    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea el DAO con su propia factoría a partir de la URL JDBC.
     *
     * @param jdbcUrl URL de la base de datos SQLite
     */
    public EntradaDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    /**
     * Crea el DAO usando una factoría externa, que no se cierra al cerrar el DAO.
     *
     * @param factory factoría compartida
     */
    public EntradaDao(EntityManagerFactory factory) {
        managerFactory = factory;
        this.ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Entrada dato) {
        return dato != null && dato.getIdSesion() != null && dato.getIdEspectador() != null
                && dato.getFechaCompra() != null && dato.getEstado() != null
                && List.of("PAGADA", "USADA", "CANCELADA").contains(dato.getEstado());
    }

    /**
     * Lista todas las entradas ordenadas por id.
     *
     * @return entradas; lista vacía si no hay datos o hay error
     */
    public List<Entrada> listar() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT entrada from EntradaEntity entrada order by entrada.idEntrada",
                            EntradaEntity.class)
                    .getResultStream().map(EntradaMapper::desdeEntidad).toList();

        } catch (RuntimeException e) {
            registrar("listar entrada", e);
            return List.of();
        }
    }

    /**
     * Busca una entrada por su id.
     *
     * @param id id de la entrada
     * @return la entrada, o vacío si no existe, el id no es válido o hay error
     */
    public Optional<Entrada> buscarPorId(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(EntradaEntity.class, id)).map(EntradaMapper::desdeEntidad);

        } catch (RuntimeException e) {
            registrar("buscar entrada por id", e);
            return Optional.empty();
        }
    }

    /**
     * Inserta una entrada sin control de aforo (para ventas usar
     * SesionDao.venderEntrada).
     *
     * @param dato entrada con sesión, espectador, fecha de compra y estado válido
     * @return id generado, o 0 si los datos no son válidos o falla la BD (FK
     *         inexistente, entrada duplicada)
     */
    public int insertar(Entrada dato) {
        if (!validar(dato)) {
            return 0;
        }

        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                EntradaEntity entidad = EntradaMapper.aEntidad(dato);
                entidad.setIdEntrada(null);
                entityManager.persist(entidad);
                tx.commit();
                dato.setIdEntrada(entidad.getIdEntrada());
                return dato.getIdEntrada();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("Insertar entrada", e);
                return 0;
            }
        }
    }

    /**
     * Actualiza una entrada existente.
     *
     * @param dato entrada con id y datos nuevos
     * @return 1 si se actualiza, 0 si no existe, los datos no son válidos o falla
     *         la BD
     */
    public int actualizar(Entrada dato) {
        if (!validar(dato) || dato.getIdEntrada() == null) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                EntradaEntity entrada = entityManager.find(EntradaEntity.class, dato.getIdEntrada());
                if (entrada == null) {
                    tx.rollback();
                    return 0;
                }
                entrada.setEstado(dato.getEstado());
                entrada.setFechaCompra(dato.getFechaCompra());
                entrada.setIdEspectador(dato.getIdEspectador());
                entrada.setIdSesion(dato.getIdSesion());
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("Actualizar entrada", e);
                return 0;
            }
        }
    }

    /**
     * Elimina una entrada.
     *
     * @param id id de la entrada
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
                EntradaEntity entrada = entityManager.find(EntradaEntity.class, id);
                if (entrada == null) {
                    tx.rollback();
                    return 0;
                }
                entityManager.remove(entrada);
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("eliminar entrada", e);
                return 0;
            }
        }
    }

    /**
     * Entradas con nombre del espectador y título de la película (C08).
     *
     * @return filas {id_entrada, espectador, pelicula, estado} ordenadas por id;
     *         vacía si hay error
     */
    public List<Object[]> entradasConDetalles() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT e.idEntrada, es.nombre, p.titulo, e.estado "
                            + "FROM EntradaEntity e, EspectadorEntity es, SesionEntity s, PeliculaEntity p "
                            + "WHERE e.idEspectador = es.idEspectador AND e.idSesion = s.idSesion "
                            + "AND s.idPelicula = p.idPelicula ORDER BY e.idEntrada",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("entradas con detalles", e);
            return List.of();
        }
    }

    /**
     * Historial de entradas de un espectador (C24).
     *
     * @param idEspectador id del espectador
     * @return filas {pelicula, fecha_sesion, estado_entrada, precio} ordenadas por
     *         fecha y película; vacía si no hay o hay error
     */
    public List<Object[]> entradasPorEspectador(int idEspectador) {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT p.titulo, s.fecha, e.estado, s.precio FROM EntradaEntity e, SesionEntity s, PeliculaEntity p "
                            + "WHERE e.idSesion = s.idSesion AND s.idPelicula = p.idPelicula AND e.idEspectador = ?1 "
                            + "ORDER BY s.fecha, p.titulo",
                            Object[].class)
                    .setParameter(1, idEspectador)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("entradas por espectador", e);
            return List.of();
        }
    }

    /**
     * Ventas diarias excluyendo entradas canceladas (C23).
     *
     * @return filas {fecha_compra, entradas_validas, total} ordenadas por fecha;
     *         vacía si hay error
     */
    public List<Object[]> ventasPorDia() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT e.fechaCompra, COUNT(e), SUM(s.precio) FROM EntradaEntity e, SesionEntity s "
                            + "WHERE e.idSesion = s.idSesion AND e.estado IN ('PAGADA', 'USADA') "
                            + "GROUP BY e.fechaCompra ORDER BY e.fechaCompra",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("ventas por dia", e);
            return List.of();
        }
    }

    /**
     * Cancela una entrada en una única transacción, sin borrarla (C30).
     *
     * @param idEntrada id de la entrada
     * @return 1 si se cancela, 0 si no existe, ya estaba cancelada o hay error
     */
    public int cancelarEntrada(int idEntrada) {
        if (idEntrada <= 0) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                EntradaEntity entrada = entityManager.find(EntradaEntity.class, idEntrada);
                if (entrada == null || "CANCELADA".equals(entrada.getEstado())) {
                    tx.rollback();
                    return 0;
                }
                entrada.setEstado("CANCELADA");
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("cancelar entrada", e);
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
