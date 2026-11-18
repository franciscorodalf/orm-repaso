package es.examen.sportrent.model;

public class Equipo {
    private String codigo;
    private String nombre;
    private String categoria;
    private java.math.BigDecimal precioDia;
    private String estado;
    public Equipo() { }
    public Equipo(String codigo, String nombre, String categoria, java.math.BigDecimal precioDia, String estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioDia = precioDia;
        this.estado = estado;
    }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public java.math.BigDecimal getPrecioDia() { return precioDia; }
    public String getEstado() { return estado; }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public void setPrecioDia(java.math.BigDecimal precioDia) {
        this.precioDia = precioDia;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
}