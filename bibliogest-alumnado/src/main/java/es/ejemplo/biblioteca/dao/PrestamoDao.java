package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Prestamo;
import es.ejemplo.biblioteca.orm.mapper.PrestamoMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import es.ejemplo.biblioteca.orm.entity.LibroEntity;
import es.ejemplo.biblioteca.orm.entity.PrestamoEntity;
import es.ejemplo.biblioteca.orm.entity.SocioEntity;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PrestamoDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(PrestamoDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public PrestamoDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    public PrestamoDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Prestamo dato) {
        return dato != null && dato.getIdSocio() != null && dato.getIdLibro() != null && dato.getFechaPrestamo() != null
                && dato.getFechaLimite() != null && dato.getFechaLimite().compareTo(dato.getFechaPrestamo()) >= 0
                && (dato.getFechaDevolucion() == null
                        || dato.getFechaDevolucion().compareTo(dato.getFechaPrestamo()) >= 0);
    }

    public List<Prestamo> listar() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT prestamo from PrestamoEntity prestamo", PrestamoEntity.class)
                    .getResultStream().map(PrestamoMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("listar prestamo", e);
        }
        return List.of();
    }

    public Optional<Prestamo> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();

        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(PrestamoEntity.class, id)).map(PrestamoMapper::toDomain);

        } catch (RuntimeException e) {
            registrar("buscar prestamo por id", e);
        }
        return Optional.empty();
    }

    /**
     * private Integer idPrestamo;
     * private Integer idSocio;
     * private Integer idLibro;
     * private String fechaPrestamo;
     * private String fechaLimite;
     * private String fechaDevolucion;
     */
    public int insertar(Prestamo dato) {
        if (!validar(dato)) {
            return 0;
        }

        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                PrestamoEntity entidad = PrestamoMapper.toEntity(dato);
                entityManager.persist(entidad);
                dato.setIdPrestamo(entidad.getIdPrestamo());
                tx.commit();
                return dato.getIdPrestamo();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("Insertar prestamo", e);

            }
            return 0;
        }
    }

    /**
     * private Integer idPrestamo;
     * private Integer idSocio;
     * private Integer idLibro;
     * private String fechaPrestamo;
     * private String fechaLimite;
     * private String fechaDevolucion;
     */
    public int actualizar(Prestamo dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                PrestamoEntity prestamo = entityManager.find(PrestamoEntity.class, dato.getIdPrestamo());
                if (prestamo == null) {
                    return 0;
                }

                PrestamoEntity prestamoEntityActualizado = PrestamoMapper.toEntity(dato);
                prestamo.setIdSocio(prestamoEntityActualizado.getIdSocio());
                prestamo.setIdLibro(prestamoEntityActualizado.getIdLibro());
                prestamo.setFechaPrestamo(prestamoEntityActualizado.getFechaPrestamo());
                prestamo.setFechaLimite(prestamoEntityActualizado.getFechaLimite());
                prestamo.setFechaDevolucion(prestamoEntityActualizado.getFechaDevolucion());
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("Actualizar prestamo", e);

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
                PrestamoEntity prestamo = entityManager.find(PrestamoEntity.class, id);
                if (prestamo == null) {
                    return 0;
                }
                entityManager.remove(prestamo);
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("eliminar prestamo", e);

            }
            return 0;
        }
    }

    public List<Prestamo> listarPorSocio(int idSocio) {
        if (idSocio < 0) {
            return List.of();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT prestamo from PrestamoEntity prestamo where prestamo.idSocio = :id",
                            PrestamoEntity.class)
                    .setParameter("id", idSocio)
                    .getResultStream().map(PrestamoMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("buscar por socio  de prestamo", e);
        }
        return List.of();
    }

    public List<Prestamo> listarActivos() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT prestamo from PrestamoEntity prestamo where prestamo.fechaDevolucion IS NULL",
                            PrestamoEntity.class)
                    .getResultStream().map(PrestamoMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("buscar activos e prestamo", e);
        }
        return List.of();
    }

    public List<Prestamo> listarVencidos(String fecha) {
        if (fecha == null) {
            return List.of();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT prestamo FROM PrestamoEntity prestamo "
                                    + "WHERE prestamo.fechaDevolucion IS NULL AND prestamo.fechaLimite < :fecha",
                            PrestamoEntity.class)
                    .setParameter("fecha", fecha)
                    .getResultStream().map(PrestamoMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("listar prestamos vencidos", e);
        }
        return List.of();
    }

    public long contarPrestamosPorLibro(int idLibro) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }

    public List<Object[]> prestamosConSocioYLibro() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Object[]> rankingMensual(String anioMes, int limite) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public int registrarPrestamo(int idSocio, int idLibro, String inicio, String limite) {
        Prestamo dato = new Prestamo(null, idSocio, idLibro, inicio, limite, null);
        if (idSocio <= 0 || idLibro <= 0 || !validar(dato)) {
            return 0;
        }
        synchronized (this) {
            try (var entityManager = managerFactory.createEntityManager()) {
                var tx = entityManager.getTransaction();
                try {
                    tx.begin();

                    SocioEntity socio = entityManager.find(SocioEntity.class, idSocio);
                    if (socio == null || !Boolean.TRUE.equals(socio.getActivo())) {
                        tx.rollback();
                        return 0;
                    }

                    LibroEntity libro = entityManager.find(LibroEntity.class, idLibro);
                    if (libro == null) {
                        tx.rollback();
                        return 0;
                    }

                    Long activos = entityManager.createQuery(
                            "SELECT COUNT(p) FROM PrestamoEntity p WHERE p.idLibro = :idLibro AND p.fechaDevolucion IS NULL",
                            Long.class)
                            .setParameter("idLibro", idLibro)
                            .getSingleResult();
                    if (libro.getEjemplares() == null || activos >= libro.getEjemplares()) {
                        tx.rollback();
                        return 0;
                    }

                    PrestamoEntity entidad = PrestamoMapper.toEntity(dato);
                    entityManager.persist(entidad);
                    tx.commit();
                    return entidad.getIdPrestamo();
                } catch (RuntimeException e) {
                    if (tx.isActive()) {
                        tx.rollback();
                    }
                    registrar("registrar prestamo", e);
                }
                return 0;
            }
        }
    }

    public int devolver(int id, String fecha) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }

    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen())
            managerFactory.close();
    }
}
