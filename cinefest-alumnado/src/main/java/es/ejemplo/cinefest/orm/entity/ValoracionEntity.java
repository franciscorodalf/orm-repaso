package es.ejemplo.cinefest.orm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "valoraciones")
public class ValoracionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valoracion")
    private Integer idValoracion;
    @Column(name = "id_pelicula")
    private Integer idPelicula;
    @Column(name = "id_espectador")
    private Integer idEspectador;
    @Column(name = "puntuacion")
    private Integer puntuacion;
    @Column(name = "comentario")
    private String comentario;
    @Column(name = "fecha")
    private String fecha;
    public ValoracionEntity() {}
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
