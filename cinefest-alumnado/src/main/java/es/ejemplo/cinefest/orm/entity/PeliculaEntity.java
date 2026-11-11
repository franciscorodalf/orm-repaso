package es.ejemplo.cinefest.orm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "peliculas")
public class PeliculaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pelicula")
    private Integer idPelicula;
    @Column(name = "titulo")
    private String titulo;
    @Column(name = "genero")
    private String genero;
    @Column(name = "duracion_min")
    private Integer duracionMin;
    @Column(name = "pais")
    private String pais;
    @Column(name = "anio")
    private Integer anio;
    public PeliculaEntity() {}
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
