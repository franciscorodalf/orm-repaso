package es.ejemplo.cinefest.orm.mapper;

import es.ejemplo.cinefest.modelo.Sala;
import es.ejemplo.cinefest.orm.entity.SalaEntity;

public final class SalaMapper {
    private SalaMapper() {}
    public static Sala desdeEntidad(SalaEntity source) {
        if (source == null) return null;
        Sala result = new Sala();
        result.setIdSala(source.getIdSala());
        result.setNombre(source.getNombre());
        result.setAforo(source.getAforo());
        result.setIdSede(source.getIdSede());
        return result;
    }
    public static SalaEntity aEntidad(Sala source) {
        if (source == null) return null;
        SalaEntity result = new SalaEntity();
        result.setIdSala(source.getIdSala());
        result.setNombre(source.getNombre());
        result.setAforo(source.getAforo());
        result.setIdSede(source.getIdSede());
        return result;
    }
}
