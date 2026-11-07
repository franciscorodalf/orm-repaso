package es.ejemplo.biblioteca.dao;
import es.ejemplo.biblioteca.modelo.Socio;

import es.ejemplo.biblioteca.orm.mapper.SocioMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SocioDao implements AutoCloseable {
    private static final Logger LOG=Logger.getLogger(SocioDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;
    
    public SocioDao(String url) {managerFactory=OrmFactory.crear(url);ownFactory=true;}
    
    public SocioDao(EntityManagerFactory factory) {managerFactory=factory;ownFactory=false;}
    private void registrar(String metodo,Exception ex) {LOG.log(Level.WARNING,metodo+" no se pudo completar: "+ex.getMessage());}
    
    public List<Socio> listar() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public Optional<Socio> buscarPorId(Integer id) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return Optional.empty();
    }
    
    public int insertar(Socio dato) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    
    public int actualizar(Socio dato) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    
    public int eliminar(Integer id) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    private boolean validar(Socio dato) {return dato != null && dato.getNombre()!=null && !dato.getNombre().isBlank() && dato.getEmail()!=null && !dato.getEmail().isBlank() && dato.getFechaAlta()!=null && dato.getActivo()!=null;}
    
    public List<Socio> listarActivos() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public Optional<Socio> buscarPorEmail(String email) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
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


    
    @Override public void close() {if(ownFactory && managerFactory.isOpen())managerFactory.close();}
}
