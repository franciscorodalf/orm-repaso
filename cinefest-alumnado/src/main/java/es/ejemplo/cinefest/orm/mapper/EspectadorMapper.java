package es.ejemplo.cinefest.orm.mapper;

import es.ejemplo.cinefest.modelo.Espectador;
import es.ejemplo.cinefest.orm.entity.EspectadorEntity;

public final class EspectadorMapper {
    private EspectadorMapper() {}
    public static Espectador desdeEntidad(EspectadorEntity source) {
        if (source == null) return null;
        Espectador result = new Espectador();
        result.setIdEspectador(source.getIdEspectador());
        result.setNombre(source.getNombre());
        result.setEmail(source.getEmail());
        result.setFechaAlta(source.getFechaAlta());
        result.setActivo(source.getActivo());
        return result;
    }
    public static EspectadorEntity aEntidad(Espectador source) {
        if (source == null) return null;
        EspectadorEntity result = new EspectadorEntity();
        result.setIdEspectador(source.getIdEspectador());
        result.setNombre(source.getNombre());
        result.setEmail(source.getEmail());
        result.setFechaAlta(source.getFechaAlta());
        result.setActivo(source.getActivo());
        return result;
    }
}
