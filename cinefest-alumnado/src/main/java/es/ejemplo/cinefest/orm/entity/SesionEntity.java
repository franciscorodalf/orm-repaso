package es.ejemplo.cinefest.orm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sesiones")
public class SesionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion")
    private Integer idSesion;
    @Column(name = "id_pelicula")
    private Integer idPelicula;
    @Column(name = "id_sala")
    private Integer idSala;
    @Column(name = "fecha")
    private String fecha;
    @Column(name = "hora")
    private String hora;
    @Column(name = "precio")
    private Double precio;
    @Column(name = "estado")
    private String estado;
    public SesionEntity() {}
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
