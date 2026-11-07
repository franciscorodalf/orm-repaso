package es.ejemplo.biblioteca.orm.mapper;
import es.ejemplo.biblioteca.modelo.Reserva;
import es.ejemplo.biblioteca.orm.entity.ReservaEntity;

public final class ReservaMapper {
    private ReservaMapper() {}
    
    public static ReservaEntity toEntity(Reserva dato) {
        ReservaEntity entidad = new ReservaEntity();
        entidad.setIdReserva(dato.getIdReserva());
        entidad.setIdSocio(dato.getIdSocio());
        entidad.setIdLibro(dato.getIdLibro());
        entidad.setFechaReserva(dato.getFechaReserva());
        entidad.setEstado(dato.getEstado());
        return entidad;
    }
    
    public static Reserva toDomain(ReservaEntity entidad) {
        return new Reserva(entidad.getIdReserva(), entidad.getIdSocio(), entidad.getIdLibro(), entidad.getFechaReserva(), entidad.getEstado());
    }
}
