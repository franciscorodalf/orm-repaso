package es.ejemplo.cinefest.modelo;

public class Pelicula {
    private Integer idPelicula;
    private String titulo;
    private String genero;
    private Integer duracionMin;
    private String pais;
    private Integer anio;
    public Pelicula() {}
    public Integer getIdPelicula() { return idPelicula; }
    public void setIdPelicula(Integer value) { this.idPelicula = value; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String value) { this.titulo = value; }
    public String getGenero() { return genero; }
    public void setGenero(String value) { this.genero = value; }
    public Integer getDuracionMin() { return duracionMin; }
    public void setDuracionMin(Integer value) { this.duracionMin = value; }
    public String getPais() { return pais; }
    public void setPais(String value) { this.pais = value; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer value) { this.anio = value; }
}
