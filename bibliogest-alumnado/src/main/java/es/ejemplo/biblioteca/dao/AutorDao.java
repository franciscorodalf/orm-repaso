package es.ejemplo.biblioteca.dao;
import es.ejemplo.biblioteca.modelo.Autor;

import es.ejemplo.biblioteca.orm.mapper.AutorMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class AutorDao implements AutoCloseable {
    private static final Logger LOG=Logger.getLogger(AutorDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;
    
    public AutorDao(String url) {managerFactory=OrmFactory.crear(url);ownFactory=true;}
    
    public AutorDao(EntityManagerFactory factory) {managerFactory=factory;ownFactory=false;}
    private void registrar(String metodo,Exception ex) {LOG.log(Level.WARNING,metodo+" no se pudo completar: "+ex.getMessage());}
    
    public List<Autor> listar() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public Optional<Autor> buscarPorId(Integer id) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return Optional.empty();
    }
    
    public int insertar(Autor dato) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    
    public int actualizar(Autor dato) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    
    public int eliminar(Integer id) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    private boolean validar(Autor dato) {return dato != null && dato.getNombre()!=null && !dato.getNombre().isBlank();}
    
    public List<Autor> buscarPorNacionalidad(String nacionalidad) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public long contarLibrosPorAutor(int idAutor) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
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


    
    @Override public void close() {if(ownFactory && managerFactory.isOpen())managerFactory.close();}
}
