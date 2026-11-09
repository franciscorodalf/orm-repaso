package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Socio;

import es.ejemplo.biblioteca.orm.mapper.SocioMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import es.ejemplo.biblioteca.orm.entity.SocioEntity;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SocioDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(SocioDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public SocioDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    public SocioDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Socio dato) {
        return dato != null && dato.getNombre() != null && !dato.getNombre().isBlank() && dato.getEmail() != null
                && !dato.getEmail().isBlank() && dato.getFechaAlta() != null && dato.getActivo() != null;
    }

    public List<Socio> listar() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT socio from SocioEntity socio order by socio.nombre", SocioEntity.class)
                    .getResultStream().map(SocioMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("listar Socio", e);
        }
        return List.of();
    }

    public Optional<Socio> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();

        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(SocioEntity.class, id)).map(SocioMapper::toDomain);

        } catch (RuntimeException e) {
            registrar("buscar socio por id", e);
        }
        return Optional.empty();
    }

    public int insertar(Socio dato) {
        if (!validar(dato)) {
            return 0;
        }

        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                SocioEntity entidad = SocioMapper.toEntity(dato);
                entityManager.persist(entidad);
                dato.setIdSocio(entidad.getIdSocio());
                tx.commit();
                return dato.getIdSocio();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("Insertar socio", e);

            }
            return 0;
        }
    }

    public int actualizar(Socio dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                SocioEntity socio = entityManager.find(SocioEntity.class, dato.getIdSocio());
                if (socio == null) {
                    return 0;
                }

                SocioEntity socioEntityActualizado = SocioMapper.toEntity(dato);
                socio.setNombre(socioEntityActualizado.getNombre());
                socio.setEmail(socioEntityActualizado.getEmail());
                socio.setTelefono(socioEntityActualizado.getTelefono());
                socio.setFechaAlta(socioEntityActualizado.getFechaAlta());
                socio.setActivo(socioEntityActualizado.getActivo());
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("Actualizar socio", e);

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
                SocioEntity socio = entityManager.find(SocioEntity.class, id);
                if (socio == null) {
                    return 0;
                }
                entityManager.remove(socio);
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("eliminar socio", e);

            }
            return 0;
        }
    }

    public List<Socio> listarActivos() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT socio from SocioEntity socio where socio.activo = :activo order by socio.nombre",
                            SocioEntity.class)
                    .setParameter("activo", true)
                    .getResultStream().map(SocioMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("listar activos de Socio", e);
        }
        return List.of();
    }

    public Optional<Socio> buscarPorEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT socio from SocioEntity socio where socio.email = :email order by socio.nombre",
                            SocioEntity.class)
                    .setParameter("email", email)
                    .getResultStream().map(SocioMapper::toDomain).findFirst();

        } catch (RuntimeException e) {
            registrar("buscar email de Socio", e);
        }
        return Optional.empty();
    }

    public List<Socio> listarConPrestamosVencidos(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return List.of();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT socio FROM SocioEntity socio WHERE EXISTS ("
                                    + "SELECT p FROM PrestamoEntity p WHERE p.socio = socio "
                                    + "AND p.fechaDevolucion IS NULL AND p.fechaLimite < :fecha) "
                                    + "ORDER BY socio.nombre",
                            SocioEntity.class)
                    .setParameter("fecha", fecha)
                    .getResultStream().map(SocioMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("listar socios con prestamos vencidos", e);
        }
        return List.of();
    }

    public List<Socio> sociosSinPrestamos() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT socio FROM SocioEntity socio WHERE NOT EXISTS ("
                                    + "SELECT p FROM PrestamoEntity p WHERE p.socio = socio) "
                                    + "ORDER BY socio.nombre",
                            SocioEntity.class)
                    .getResultStream().map(SocioMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("listar socios sin prestamos", e);
        }
        return List.of();
    }

    public List<Object[]> rankingSocios(int limite) {
        if (limite <= 0) {
            return List.of();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT socio.idSocio, socio.nombre, COUNT(p) FROM PrestamoEntity p JOIN p.socio socio "
                                    + "GROUP BY socio.idSocio, socio.nombre "
                                    + "ORDER BY COUNT(p) DESC, socio.nombre",
                            Object[].class)
                    .setMaxResults(limite)
                    .getResultList();

        } catch (RuntimeException e) {
            registrar("ranking de socios", e);
        }
        return List.of();
    }

    public List<Object[]> historialSocio(int idSocio) {
        if (idSocio <= 0) {
            return List.of();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT p.idPrestamo, libro.titulo, p.fechaPrestamo, p.fechaLimite, p.fechaDevolucion, "
                                    + "CASE WHEN p.fechaDevolucion IS NULL THEN 'PENDIENTE' "
                                    + "WHEN p.fechaDevolucion > p.fechaLimite THEN 'DEVUELTO CON RETRASO' "
                                    + "ELSE 'DEVUELTO EN PLAZO' END "
                                    + "FROM PrestamoEntity p JOIN p.libro libro "
                                    + "WHERE p.idSocio = :idSocio ORDER BY p.fechaPrestamo",
                            Object[].class)
                    .setParameter("idSocio", idSocio)
                    .getResultList();

        } catch (RuntimeException e) {
            registrar("historial de socio", e);
        }
        return List.of();
    }

    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen())
            managerFactory.close();
    }
}
