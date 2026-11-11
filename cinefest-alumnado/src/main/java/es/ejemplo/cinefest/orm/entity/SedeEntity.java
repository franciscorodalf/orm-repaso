package es.ejemplo.cinefest.orm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sedes")
public class SedeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sede")
    private Integer idSede;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "municipio")
    private String municipio;
    public SedeEntity() {}
    public Integer getIdSede() { return idSede; }
    public void setIdSede(Integer value) { this.idSede = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public String getMunicipio() { return municipio; }
    public void setMunicipio(String value) { this.municipio = value; }
}
