package es.ejemplo.biblioteca.orm.mapper;
import es.ejemplo.biblioteca.modelo.Socio;
import es.ejemplo.biblioteca.orm.entity.SocioEntity;

public final class SocioMapper {
    private SocioMapper() {}
    
    public static SocioEntity toEntity(Socio dato) {
        SocioEntity entidad = new SocioEntity();
        entidad.setIdSocio(dato.getIdSocio());
        entidad.setNombre(dato.getNombre());
        entidad.setEmail(dato.getEmail());
        entidad.setTelefono(dato.getTelefono());
        entidad.setFechaAlta(dato.getFechaAlta());
        entidad.setActivo(dato.getActivo());
        return entidad;
    }
    
    public static Socio toDomain(SocioEntity entidad) {
        return new Socio(entidad.getIdSocio(), entidad.getNombre(), entidad.getEmail(), entidad.getTelefono(), entidad.getFechaAlta(), entidad.getActivo());
    }
}
