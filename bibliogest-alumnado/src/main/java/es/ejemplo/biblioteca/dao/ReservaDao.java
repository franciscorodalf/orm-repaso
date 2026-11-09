package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Reserva;
import es.ejemplo.biblioteca.orm.mapper.ReservaMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import es.ejemplo.biblioteca.orm.entity.ReservaEntity;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ReservaDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(ReservaDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public ReservaDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    public ReservaDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Reserva dato) {
        return dato != null && dato.getIdSocio() != null && dato.getIdLibro() != null && dato.getFechaReserva() != null
                && dato.getEstado() != null && (dato.getEstado().equals("PENDIENTE")
                        || dato.getEstado().equals("ATENDIDA") || dato.getEstado().equals("CANCELADA"));
    }

    public List<Reserva> listar() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT reserva from ReservaEntity reserva", ReservaEntity.class)
                    .getResultStream().map(ReservaMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("listar reserva", e);
        }
        return List.of();
    }

    public Optional<Reserva> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();

        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(ReservaEntity.class, id)).map(ReservaMapper::toDomain);

        } catch (RuntimeException e) {
            registrar("buscar reserva por id", e);
        }
        return Optional.empty();
    }

    public int insertar(Reserva dato) {
        if (!validar(dato)) {
            return 0;
        }

        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                ReservaEntity entidad = ReservaMapper.toEntity(dato);
                entityManager.persist(entidad);
                dato.setIdReserva(entidad.getIdReserva());
                tx.commit();
                return dato.getIdReserva();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("Insertar reserva", e);

            }
            return 0;
        }
    }

    /**
     * private Integer idReserva;
     * private Integer idSocio;
     * private Integer idLibro;
     * private String fechaReserva;
     * private String estado;
     */
    public int actualizar(Reserva dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                ReservaEntity reserva = entityManager.find(ReservaEntity.class, dato.getIdReserva());
                if (reserva == null) {
                    return 0;
                }

                ReservaEntity reservaEntityActualizado = ReservaMapper.toEntity(dato);
                reserva.setIdSocio(reservaEntityActualizado.getIdSocio());
                reserva.setIdLibro(reservaEntityActualizado.getIdLibro());
                reserva.setFechaReserva(reservaEntityActualizado.getFechaReserva());
                reserva.setEstado(reservaEntityActualizado.getEstado());
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("Actualizar reserva", e);

            }
            return 0;
        }
    }

    public int eliminar(Integer id) {
        if (id == null) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                ReservaEntity reserva = entityManager.find(ReservaEntity.class, id);
                if (reserva == null) {
                    return 0;
                }
                entityManager.remove(reserva);
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("eliminar reserva", e);
            }
            return 0;
        }
    }

    public List<Reserva> listarPendientes() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT reserva from ReservaEntity reserva where reserva.estado = :estado",
                            ReservaEntity.class)
                    .setParameter("estado", "PENDIENTE")
                    .getResultStream().map(ReservaMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("listar pendientes de reserva", e);
        }
        return List.of();
    }

    public List<Reserva> buscarPorSocio(int idSocio) {
        if (idSocio < 0) {
            return List.of();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT reserva from ReservaEntity reserva where reserva.idSocio = :id",
                            ReservaEntity.class)
                    .setParameter("id", idSocio)
                    .getResultStream().map(ReservaMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("buscar socio de reserva", e);
        }
        return List.of();
    }

    public List<Reserva> listarPorLibro(int idLibro) {
        if (idLibro < 0) {
            return List.of();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT reserva from ReservaEntity reserva where reserva.idLibro = :id",
                            ReservaEntity.class)
                    .setParameter("id", idLibro)
                    .getResultStream().map(ReservaMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("buscar libro de reserva", e);
        }
        return List.of();
    }

    public long contarReservasPendientes() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT reserva from ReservaEntity reserva where reserva.estado = :estado",
                            ReservaEntity.class)
                    .setParameter("estado", "Pendiente")
                    .getResultStream().map(ReservaMapper::toDomain).count();

        } catch (RuntimeException e) {
            registrar("buscar libro de reserva", e);
        }
        return 0;
    }

    public List<Object[]> reservasConDetalles() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT r.idReserva, socio.nombre, libro.titulo, autor.nombre, r.fechaReserva "
                                    + "FROM ReservaEntity r JOIN r.socio socio JOIN r.libro libro JOIN libro.autor autor "
                                    + "WHERE r.estado = :estado ORDER BY r.fechaReserva",
                            Object[].class)
                    .setParameter("estado", "PENDIENTE")
                    .getResultList();

        } catch (RuntimeException e) {
            registrar("reservas con detalles", e);
        }
        return List.of();
    }

    public List<Object[]> prioridadReservas() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT libro.idLibro, libro.titulo, r.idReserva, socio.nombre, r.fechaReserva "
                                    + "FROM ReservaEntity r JOIN r.libro libro JOIN r.socio socio "
                                    + "WHERE r.estado = :estado AND r.fechaReserva = ("
                                    + "SELECT MIN(r2.fechaReserva) FROM ReservaEntity r2 "
                                    + "WHERE r2.idLibro = r.idLibro AND r2.estado = :estado) "
                                    + "ORDER BY r.fechaReserva",
                            Object[].class)
                    .setParameter("estado", "PENDIENTE")
                    .getResultList();

        } catch (RuntimeException e) {
            registrar("prioridad de reservas", e);
        }
        return List.of();
    }

    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen())
            managerFactory.close();
    }
}
