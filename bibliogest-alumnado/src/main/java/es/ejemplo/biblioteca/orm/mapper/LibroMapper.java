package es.ejemplo.biblioteca.orm.mapper;
import es.ejemplo.biblioteca.modelo.Libro;
import es.ejemplo.biblioteca.orm.entity.LibroEntity;

public final class LibroMapper {
    private LibroMapper() {}
    
    public static LibroEntity toEntity(Libro dato) {
        LibroEntity entidad = new LibroEntity();
        entidad.setIdLibro(dato.getIdLibro());
        entidad.setTitulo(dato.getTitulo());
        entidad.setIsbn(dato.getIsbn());
        entidad.setGenero(dato.getGenero());
        entidad.setAnioPublicacion(dato.getAnioPublicacion());
        entidad.setEjemplares(dato.getEjemplares());
        entidad.setIdAutor(dato.getIdAutor());
        return entidad;
    }
    
    public static Libro toDomain(LibroEntity entidad) {
        return new Libro(entidad.getIdLibro(), entidad.getTitulo(), entidad.getIsbn(), entidad.getGenero(), entidad.getAnioPublicacion(), entidad.getEjemplares(), entidad.getIdAutor());
    }
}
