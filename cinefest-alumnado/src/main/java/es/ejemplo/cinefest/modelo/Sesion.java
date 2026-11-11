package es.ejemplo.cinefest.modelo;

public class Sesion {
    private Integer idSesion;
    private Integer idPelicula;
    private Integer idSala;
    private String fecha;
    private String hora;
    private Double precio;
    private String estado;
    public Sesion() {}
    public Integer getIdSesion() { return idSesion; }
    public void setIdSesion(Integer value) { this.idSesion = value; }
    public Integer getIdPelicula() { return idPelicula; }
    public void setIdPelicula(Integer value) { this.idPelicula = value; }
    public Integer getIdSala() { return idSala; }
    public void setIdSala(Integer value) { this.idSala = value; }
    public String getFecha() { return fecha; }
    public void setFecha(String value) { this.fecha = value; }
    public String getHora() { return hora; }
    public void setHora(String value) { this.hora = value; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double value) { this.precio = value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }
}
