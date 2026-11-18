package es.examen.sportrent.model;

import java.math.BigDecimal;

public class EquipoPrecio {
    private String codigo;
    private String nombre;
    private String categoria;
    private BigDecimal precioDia;
    private Double mediaCategoria;

    public EquipoPrecio() {
    }

    public EquipoPrecio(String codigo, String nombre, String categoria, BigDecimal precioDia, Double mediaCategoria) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioDia = precioDia;
        this.mediaCategoria = mediaCategoria;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecioDia() {
        return precioDia;
    }

    public void setPrecioDia(BigDecimal precioDia) {
        this.precioDia = precioDia;
    }

    public Double getMediaCategoria() {
        return mediaCategoria;
    }

    public void setMediaCategoria(Double mediaCategoria) {
        this.mediaCategoria = mediaCategoria;
    }

}
