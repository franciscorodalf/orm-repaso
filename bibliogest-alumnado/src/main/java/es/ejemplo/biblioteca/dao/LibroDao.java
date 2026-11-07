package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Libro;
import es.ejemplo.biblioteca.orm.mapper.AutorMapper;
import es.ejemplo.biblioteca.orm.mapper.LibroMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import es.ejemplo.biblioteca.orm.entity.AutorEntity;
import es.ejemplo.biblioteca.orm.entity.LibroEntity;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LibroDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(LibroDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public LibroDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    public LibroDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Libro dato) {
        return dato != null && dato.getTitulo() != null && !dato.getTitulo().isBlank() && dato.getIsbn() != null
                && !dato.getIsbn().isBlank() && dato.getGenero() != null && dato.getIdAutor() != null
                && dato.getEjemplares() != null && dato.getEjemplares() >= 0;
    }

    /**
     * private Integer idLibro;
     * private String titulo;
     * private String isbn;
     * private String genero;
     * private Integer anioPublicacion;
     * private Integer ejemplares;
     * private Integer idAutor;
     * 
     */
    public List<Libro> listar() {
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT libro from LibroEntity libro order by libro.titulo", LibroEntity.class)
                    .getResultStream().map(LibroMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("listar libro", e);
        }
        return List.of();
    }

    public Optional<Libro> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();

        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(LibroEntity.class, id)).map(LibroMapper::toDomain);

        } catch (RuntimeException e) {
            registrar("buscar libro por id", e);
        }
        return Optional.empty();
    }

    public int insertar(Libro dato) {
        if (!validar(dato)) {
            return 0;
        }

        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                LibroEntity entidad = LibroMapper.toEntity(dato);
                entityManager.persist(entidad);
                dato.setIdLibro(entidad.getIdLibro());
                tx.commit();
                return dato.getIdLibro();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("Insertar libro", e);

            }
            return 0;
        }
    }

    public int actualizar(Libro dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            var tx = entityManager.getTransaction();
            try {
                tx.begin();
                LibroEntity libro = entityManager.find(LibroEntity.class, dato.getIdLibro());
                if (libro == null) {
                    return 0;
                }
                LibroEntity libroEntityActualizado = LibroMapper.toEntity(dato);
                libro.setTitulo(libroEntityActualizado.getTitulo());
                libro.setIsbn(libroEntityActualizado.getIsbn());
                libro.setGenero(libroEntityActualizado.getGenero());
                libro.setAnioPublicacion(libroEntityActualizado.getAnioPublicacion());
                libro.setEjemplares(libroEntityActualizado.getEjemplares());
                libro.setIdAutor(libroEntityActualizado.getIdAutor());
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("Actualizar libro", e);

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
                LibroEntity libro = entityManager.find(LibroEntity.class, id);
                if (libro == null) {
                    return 0;
                }
                entityManager.remove(libro);
                tx.commit();
                return 1;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                registrar("eliminar libro", e);

            }
            return 0;
        }
    }

    public List<Libro> buscarPorGenero(String genero) {
        if (genero == null || genero.isBlank()) {
            return List.of();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT libro from LibroEntity libro where libro.genero = :genero order by libro.titulo",
                            LibroEntity.class)
                    .setParameter("genero", genero)
                    .getResultStream().map(LibroMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("buscar genero de libro", e);
        }
        return List.of();
    }

    public List<Libro> buscarPorAutor(int idAutor) {
        if (idAutor < 0) {
            return List.of();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT libro from LibroEntity libro where libro.idAutor = :idAutor order by libro.titulo",
                            LibroEntity.class)
                    .setParameter("idAutor", idAutor)
                    .getResultStream().map(LibroMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("buscar autor de libro", e);
        }
        return List.of();
    }

    public List<Libro> buscarPorTitulo(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT libro from LibroEntity libro where libro.titulo = :titulo order by libro.titulo",
                            LibroEntity.class)
                    .setParameter("titulo", texto)
                    .getResultStream().map(LibroMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("buscar autor de libro", e);
        }
        return List.of();
    }

    public List<Libro> listarDisponibles() {

        try (var entityManager = managerFactory.createEntityManager()) {
            return entityManager
                    .createQuery(
                            "SELECT libro FROM LibroEntity libro WHERE libro.ejemplares > (SELECT COUNT(p) FROM PrestamoEntity p WHERE p.libro = libro AND p.fechaDevolucion IS NULL) ORDER BY libro.titulo",
                            LibroEntity.class)
                    .getResultStream().map(LibroMapper::toDomain).toList();

        } catch (RuntimeException e) {
            registrar("buscar disponibles de libro", e);
        }
        return List.of();
    }

    public List<Libro> librosDesdeAnio(int anio, String genero) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<String> generos() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Libro> nuncaPrestados() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Libro> masPrestadosQueMedia() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Libro> reservadosSinDisponibilidad() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Object[]> librosConAutor() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Object[]> contarPorGenero() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Object[]> prestamosPorGeneroMinimo(long minimo) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Object[]> disponibilidadPorLibro() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Object[]> estadisticasGenero() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen())
            managerFactory.close();
    }
}
