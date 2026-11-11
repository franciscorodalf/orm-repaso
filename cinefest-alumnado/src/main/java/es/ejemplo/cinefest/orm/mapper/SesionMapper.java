package es.ejemplo.cinefest.orm.mapper;

import es.ejemplo.cinefest.modelo.Sesion;
import es.ejemplo.cinefest.orm.entity.SesionEntity;

public final class SesionMapper {
    private SesionMapper() {}
    public static Sesion desdeEntidad(SesionEntity source) {
        if (source == null) return null;
        Sesion result = new Sesion();
        result.setIdSesion(source.getIdSesion());
        result.setIdPelicula(source.getIdPelicula());
        result.setIdSala(source.getIdSala());
        result.setFecha(source.getFecha());
        result.setHora(source.getHora());
        result.setPrecio(source.getPrecio());
        result.setEstado(source.getEstado());
        return result;
    }
    public static SesionEntity aEntidad(Sesion source) {
        if (source == null) return null;
        SesionEntity result = new SesionEntity();
        result.setIdSesion(source.getIdSesion());
        result.setIdPelicula(source.getIdPelicula());
        result.setIdSala(source.getIdSala());
        result.setFecha(source.getFecha());
        result.setHora(source.getHora());
        result.setPrecio(source.getPrecio());
        result.setEstado(source.getEstado());
        return result;
    }
}
