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
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Socio> sociosSinPrestamos() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Object[]> rankingSocios(int limite) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Object[]> historialSocio(int idSocio) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen())
            managerFactory.close();
    }
}
