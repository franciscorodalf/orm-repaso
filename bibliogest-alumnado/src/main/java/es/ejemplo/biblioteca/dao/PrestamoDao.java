package es.ejemplo.biblioteca.dao;
import es.ejemplo.biblioteca.modelo.Prestamo;

import es.ejemplo.biblioteca.orm.mapper.PrestamoMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PrestamoDao implements AutoCloseable {
    private static final Logger LOG=Logger.getLogger(PrestamoDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;
    
    public PrestamoDao(String url) {managerFactory=OrmFactory.crear(url);ownFactory=true;}
    
    public PrestamoDao(EntityManagerFactory factory) {managerFactory=factory;ownFactory=false;}
    private void registrar(String metodo,Exception ex) {LOG.log(Level.WARNING,metodo+" no se pudo completar: "+ex.getMessage());}
    
    public List<Prestamo> listar() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public Optional<Prestamo> buscarPorId(Integer id) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return Optional.empty();
    }
    
    public int insertar(Prestamo dato) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    
    public int actualizar(Prestamo dato) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    
    public int eliminar(Integer id) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    private boolean validar(Prestamo dato) {return dato != null && dato.getIdSocio()!=null && dato.getIdLibro()!=null && dato.getFechaPrestamo()!=null && dato.getFechaLimite()!=null && dato.getFechaLimite().compareTo(dato.getFechaPrestamo())>=0 && (dato.getFechaDevolucion()==null || dato.getFechaDevolucion().compareTo(dato.getFechaPrestamo())>=0);}
    
    public List<Prestamo> listarPorSocio(int idSocio) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public List<Prestamo> listarActivos() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public List<Prestamo> listarVencidos(String fecha) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
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
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    
    public int devolver(int id, String fecha) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }

    
    @Override public void close() {if(ownFactory && managerFactory.isOpen())managerFactory.close();}
}
