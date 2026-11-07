package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Autor;

import es.ejemplo.biblioteca.orm.mapper.AutorMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import es.ejemplo.biblioteca.orm.entity.AutorEntity;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class AutorDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(AutorDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public AutorDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    public AutorDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Autor dato) {
        return dato != null && dato.getNombre() != null && !dato.getNombre().isBlank();
    }

    public List<Autor> listar() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT autor from AutorEntity autor order by autor.nombre", AutorEntity.class)
                    .getResultStream().map(AutorMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("listar autor", e);
        }
        return List.of();
    }

    public Optional<Autor> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();

        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(AutorEntity.class, id)).map(AutorMapper::toDomain);

        } catch (RuntimeException e) {
            registrar("buscar autor por id", e);
        }
        return Optional.empty();
    }

    /**
     * private Integer idAutor;
     * private String nombre;
     * private String nacionalidad;
     * private Integer anioNacimiento;
     */

    public int insertar(Autor dato) {
        if (!validar(dato)) {
            return 0;
        }

        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                AutorEntity entidad = AutorMapper.toEntity(dato);
                entityManager.persist(entidad);
                dato.setIdAutor(entidad.getIdAutor());
                tx.commit();
                return dato.getIdAutor();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("Insertar autor", e);

            }
            return 0;
        }
    }

    /**
     * private Integer idAutor;
     * private String nombre;
     * private String nacionalidad;
     * private Integer anioNacimiento;
     * 
     */
    public int actualizar(Autor dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                AutorEntity autor = entityManager.find(AutorEntity.class, dato.getIdAutor());
                if (autor == null) {
                    return 0;
                }

                AutorEntity autorEntityActualizado = AutorMapper.toEntity(dato);
                autor.setNombre(autorEntityActualizado.getNombre());
                autor.setNacionalidad(autorEntityActualizado.getNacionalidad());
                autor.setAnioNacimiento(autorEntityActualizado.getAnioNacimiento());
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("Actualizar autor", e);

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
                AutorEntity autor = entityManager.find(AutorEntity.class, id);
                if (autor == null) {
                    return 0;
                }
                entityManager.remove(autor);
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("eliminar autor", e);

            }
            return 0;
        }
    }

    public List<Autor> buscarPorNacionalidad(String nacionalidad) {
        if (nacionalidad == null || nacionalidad.isBlank()) {
            return List.of();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT autor from AutorEntity autor where autor.nacionalidad = :nacionalidad order by autor.nombre",
                            AutorEntity.class)
                    .setParameter("nacionalidad", nacionalidad)
                    .getResultStream().map(AutorMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("buscar nacionalidad de autor", e);
        }
        return List.of();
    }

    public long contarLibrosPorAutor(int idAutor) {
        if (idAutor < 0) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT COUNT(libro) from LibroEntity libro where libro.autor = :id",
                            AutorEntity.class)
                    .setParameter("id", idAutor)
                    .getResultStream().map(AutorMapper::toDomain).count();

        } catch (RuntimeException e) {
            registrar("buscar nacionalidad de autor", e);
        }
        return 0;
    }

    public List<Object[]> autoresConRecuento() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Autor> autoresLeidosPorTresSocios() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen())
            managerFactory.close();
    }
}
