package es.ejemplo.cinefest.modelo;

public class Valoracion {
    private Integer idValoracion;
    private Integer idPelicula;
    private Integer idEspectador;
    private Integer puntuacion;
    private String comentario;
    private String fecha;
    public Valoracion() {}
    public Integer getIdValoracion() { return idValoracion; }
    public void setIdValoracion(Integer value) { this.idValoracion = value; }
    public Integer getIdPelicula() { return idPelicula; }
    public void setIdPelicula(Integer value) { this.idPelicula = value; }
    public Integer getIdEspectador() { return idEspectador; }
    public void setIdEspectador(Integer value) { this.idEspectador = value; }
    public Integer getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Integer value) { this.puntuacion = value; }
    public String getComentario() { return comentario; }
    public void setComentario(String value) { this.comentario = value; }
    public String getFecha() { return fecha; }
    public void setFecha(String value) { this.fecha = value; }
}
