package es.ejemplo.biblioteca.orm.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name="socios")
public class SocioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_socio")
    private Integer idSocio;
    @Column(name="nombre")
    private String nombre;
    @Column(name="email", nullable=false, unique=true)
    private String email;
    @Column(name="telefono")
    private String telefono;
    @Column(name="fecha_alta")
    private String fechaAlta;
    @Column(name="activo")
    private Boolean activo;
    
    public SocioEntity() {}
    
    public Integer getIdSocio() { return idSocio; }
    
    public void setIdSocio(Integer valor) { this.idSocio = valor; }
    
    public String getNombre() { return nombre; }
    
    public void setNombre(String valor) { this.nombre = valor; }
    
    public String getEmail() { return email; }
    
    public void setEmail(String valor) { this.email = valor; }
    
    public String getTelefono() { return telefono; }
    
    public void setTelefono(String valor) { this.telefono = valor; }
    
    public String getFechaAlta() { return fechaAlta; }
    
    public void setFechaAlta(String valor) { this.fechaAlta = valor; }
    
    public Boolean getActivo() { return activo; }
    
    public void setActivo(Boolean valor) { this.activo = valor; }
}
