package es.ejemplo.biblioteca.orm.mapper;
import es.ejemplo.biblioteca.modelo.Prestamo;
import es.ejemplo.biblioteca.orm.entity.PrestamoEntity;

public final class PrestamoMapper {
    private PrestamoMapper() {}
    
    public static PrestamoEntity toEntity(Prestamo dato) {
        PrestamoEntity entidad = new PrestamoEntity();
        entidad.setIdPrestamo(dato.getIdPrestamo());
        entidad.setIdSocio(dato.getIdSocio());
        entidad.setIdLibro(dato.getIdLibro());
        entidad.setFechaPrestamo(dato.getFechaPrestamo());
        entidad.setFechaLimite(dato.getFechaLimite());
        entidad.setFechaDevolucion(dato.getFechaDevolucion());
        return entidad;
    }
    
    public static Prestamo toDomain(PrestamoEntity entidad) {
        return new Prestamo(entidad.getIdPrestamo(), entidad.getIdSocio(), entidad.getIdLibro(), entidad.getFechaPrestamo(), entidad.getFechaLimite(), entidad.getFechaDevolucion());
    }
}
