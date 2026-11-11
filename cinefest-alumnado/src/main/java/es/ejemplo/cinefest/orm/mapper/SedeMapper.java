package es.ejemplo.cinefest.orm.mapper;

import es.ejemplo.cinefest.modelo.Sede;
import es.ejemplo.cinefest.orm.entity.SedeEntity;

public final class SedeMapper {
    private SedeMapper() {}
    public static Sede desdeEntidad(SedeEntity source) {
        if (source == null) return null;
        Sede result = new Sede();
        result.setIdSede(source.getIdSede());
        result.setNombre(source.getNombre());
        result.setMunicipio(source.getMunicipio());
        return result;
    }
    public static SedeEntity aEntidad(Sede source) {
        if (source == null) return null;
        SedeEntity result = new SedeEntity();
        result.setIdSede(source.getIdSede());
        result.setNombre(source.getNombre());
        result.setMunicipio(source.getMunicipio());
        return result;
    }
}
