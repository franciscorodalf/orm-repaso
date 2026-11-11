package es.ejemplo.cinefest.orm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "espectadores")
public class EspectadorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_espectador")
    private Integer idEspectador;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "email")
    private String email;
    @Column(name = "fecha_alta")
    private String fechaAlta;
    @Column(name = "activo")
    private Integer activo;
    public EspectadorEntity() {}
    public Integer getIdEspectador() { return idEspectador; }
    public void setIdEspectador(Integer value) { this.idEspectador = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { this.email = value; }
    public String getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(String value) { this.fechaAlta = value; }
    public Integer getActivo() { return activo; }
    public void setActivo(Integer value) { this.activo = value; }
}
