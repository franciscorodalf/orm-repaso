package es.ejemplo.cinefest.modelo;

public class Entrada {
    private Integer idEntrada;
    private Integer idSesion;
    private Integer idEspectador;
    private String fechaCompra;
    private String estado;
    public Entrada() {}
    public Integer getIdEntrada() { return idEntrada; }
    public void setIdEntrada(Integer value) { this.idEntrada = value; }
    public Integer getIdSesion() { return idSesion; }
    public void setIdSesion(Integer value) { this.idSesion = value; }
    public Integer getIdEspectador() { return idEspectador; }
    public void setIdEspectador(Integer value) { this.idEspectador = value; }
    public String getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(String value) { this.fechaCompra = value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }
}
