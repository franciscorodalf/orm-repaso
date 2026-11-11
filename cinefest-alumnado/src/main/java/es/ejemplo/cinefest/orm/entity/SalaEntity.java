package es.ejemplo.cinefest.orm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "salas")
public class SalaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sala")
    private Integer idSala;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "aforo")
    private Integer aforo;
    @Column(name = "id_sede")
    private Integer idSede;
    public SalaEntity() {}
    public Integer getIdSala() { return idSala; }
    public void setIdSala(Integer value) { this.idSala = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public Integer getAforo() { return aforo; }
    public void setAforo(Integer value) { this.aforo = value; }
    public Integer getIdSede() { return idSede; }
    public void setIdSede(Integer value) { this.idSede = value; }
}
