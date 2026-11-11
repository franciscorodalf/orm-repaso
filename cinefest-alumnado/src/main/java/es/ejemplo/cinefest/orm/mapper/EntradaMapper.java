package es.ejemplo.cinefest.orm.mapper;

import es.ejemplo.cinefest.modelo.Entrada;
import es.ejemplo.cinefest.orm.entity.EntradaEntity;

public final class EntradaMapper {
    private EntradaMapper() {}
    public static Entrada desdeEntidad(EntradaEntity source) {
        if (source == null) return null;
        Entrada result = new Entrada();
        result.setIdEntrada(source.getIdEntrada());
        result.setIdSesion(source.getIdSesion());
        result.setIdEspectador(source.getIdEspectador());
        result.setFechaCompra(source.getFechaCompra());
        result.setEstado(source.getEstado());
        return result;
    }
    public static EntradaEntity aEntidad(Entrada source) {
        if (source == null) return null;
        EntradaEntity result = new EntradaEntity();
        result.setIdEntrada(source.getIdEntrada());
        result.setIdSesion(source.getIdSesion());
        result.setIdEspectador(source.getIdEspectador());
        result.setFechaCompra(source.getFechaCompra());
        result.setEstado(source.getEstado());
        return result;
    }
}
