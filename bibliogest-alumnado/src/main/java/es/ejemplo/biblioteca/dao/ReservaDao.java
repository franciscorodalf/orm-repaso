package es.ejemplo.biblioteca.dao;
import es.ejemplo.biblioteca.modelo.Reserva;

import es.ejemplo.biblioteca.orm.mapper.ReservaMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ReservaDao implements AutoCloseable {
    private static final Logger LOG=Logger.getLogger(ReservaDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;
    
    public ReservaDao(String url) {managerFactory=OrmFactory.crear(url);ownFactory=true;}
    
    public ReservaDao(EntityManagerFactory factory) {managerFactory=factory;ownFactory=false;}
    private void registrar(String metodo,Exception ex) {LOG.log(Level.WARNING,metodo+" no se pudo completar: "+ex.getMessage());}
    
    public List<Reserva> listar() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public Optional<Reserva> buscarPorId(Integer id) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return Optional.empty();
    }
    
    public int insertar(Reserva dato) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    
    public int actualizar(Reserva dato) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    
    public int eliminar(Integer id) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    private boolean validar(Reserva dato) {return dato != null && dato.getIdSocio()!=null && dato.getIdLibro()!=null && dato.getFechaReserva()!=null && dato.getEstado()!=null && (dato.getEstado().equals("PENDIENTE") || dato.getEstado().equals("ATENDIDA") || dato.getEstado().equals("CANCELADA"));}
    
    public List<Reserva> listarPendientes() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public List<Reserva> buscarPorSocio(int idSocio) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public List<Reserva> listarPorLibro(int idLibro) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public long contarReservasPendientes() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }
    
    public List<Object[]> reservasConDetalles() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public List<Object[]> prioridadReservas() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }


    
    @Override public void close() {if(ownFactory && managerFactory.isOpen())managerFactory.close();}
}
