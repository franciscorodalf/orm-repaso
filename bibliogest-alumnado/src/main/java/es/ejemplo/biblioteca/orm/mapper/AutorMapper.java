package es.ejemplo.biblioteca.orm.mapper;
import es.ejemplo.biblioteca.modelo.Autor;
import es.ejemplo.biblioteca.orm.entity.AutorEntity;

public final class AutorMapper {
    private AutorMapper() {}
    
    public static AutorEntity toEntity(Autor dato) {
        AutorEntity entidad = new AutorEntity();
        entidad.setIdAutor(dato.getIdAutor());
        entidad.setNombre(dato.getNombre());
        entidad.setNacionalidad(dato.getNacionalidad());
        entidad.setAnioNacimiento(dato.getAnioNacimiento());
        return entidad;
    }
    
    public static Autor toDomain(AutorEntity entidad) {
        return new Autor(entidad.getIdAutor(), entidad.getNombre(), entidad.getNacionalidad(), entidad.getAnioNacimiento());
    }
}
