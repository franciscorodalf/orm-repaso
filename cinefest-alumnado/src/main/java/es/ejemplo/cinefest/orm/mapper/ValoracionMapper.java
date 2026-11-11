package es.ejemplo.cinefest.orm.mapper;

import es.ejemplo.cinefest.modelo.Valoracion;
import es.ejemplo.cinefest.orm.entity.ValoracionEntity;

public final class ValoracionMapper {
    private ValoracionMapper() {}
    public static Valoracion desdeEntidad(ValoracionEntity source) {
        if (source == null) return null;
        Valoracion result = new Valoracion();
        result.setIdValoracion(source.getIdValoracion());
        result.setIdPelicula(source.getIdPelicula());
        result.setIdEspectador(source.getIdEspectador());
        result.setPuntuacion(source.getPuntuacion());
        result.setComentario(source.getComentario());
        result.setFecha(source.getFecha());
        return result;
    }
    public static ValoracionEntity aEntidad(Valoracion source) {
        if (source == null) return null;
        ValoracionEntity result = new ValoracionEntity();
        result.setIdValoracion(source.getIdValoracion());
        result.setIdPelicula(source.getIdPelicula());
        result.setIdEspectador(source.getIdEspectador());
        result.setPuntuacion(source.getPuntuacion());
        result.setComentario(source.getComentario());
        result.setFecha(source.getFecha());
        return result;
    }
}
