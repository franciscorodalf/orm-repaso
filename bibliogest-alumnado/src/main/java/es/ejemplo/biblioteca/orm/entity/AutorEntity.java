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
@Table(name="autores")
public class AutorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_autor")
    private Integer idAutor;
    @Column(name="nombre")
    private String nombre;
    @Column(name="nacionalidad")
    private String nacionalidad;
    @Column(name="anio_nacimiento")
    private Integer anioNacimiento;
    
    public AutorEntity() {}
    
    public Integer getIdAutor() { return idAutor; }
    
    public void setIdAutor(Integer valor) { this.idAutor = valor; }
    
    public String getNombre() { return nombre; }
    
    public void setNombre(String valor) { this.nombre = valor; }
    
    public String getNacionalidad() { return nacionalidad; }
    
    public void setNacionalidad(String valor) { this.nacionalidad = valor; }
    
    public Integer getAnioNacimiento() { return anioNacimiento; }
    
    public void setAnioNacimiento(Integer valor) { this.anioNacimiento = valor; }
}
