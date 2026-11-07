package es.ejemplo.biblioteca.dao;
import es.ejemplo.biblioteca.modelo.Libro;

import es.ejemplo.biblioteca.orm.mapper.LibroMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LibroDao implements AutoCloseable {
    private static final Logger LOG=Logger.getLogger(LibroDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;
    
    public LibroDao(String url) {managerFactory=OrmFactory.crear(url);ownFactory=true;}
    
    public LibroDao(EntityManagerFactory factory) {managerFactory=factory;ownFactory=false;}
    private void registrar(String metodo,Exception ex) {LOG.log(Level.WARNING,metodo+" no se pudo completar: "+ex.getMessage());}
    
    public List<Libro> listar() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public Optional<Libro> buscarPorId(Integer id) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return Optional.empty();
    }
    
    public int insertar(Libro dato) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    
    public int actualizar(Libro dato) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    
    public int eliminar(Integer id) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    private boolean validar(Libro dato) {return dato != null && dato.getTitulo()!=null && !dato.getTitulo().isBlank() && dato.getIsbn()!=null && !dato.getIsbn().isBlank() && dato.getGenero()!=null && dato.getIdAutor()!=null && dato.getEjemplares()!=null && dato.getEjemplares()>=0;}
    
    public List<Libro> buscarPorGenero(String genero) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public List<Libro> buscarPorAutor(int idAutor) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public List<Libro> buscarPorTitulo(String texto) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public List<Libro> listarDisponibles() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
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


    
    @Override public void close() {if(ownFactory && managerFactory.isOpen())managerFactory.close();}
}
