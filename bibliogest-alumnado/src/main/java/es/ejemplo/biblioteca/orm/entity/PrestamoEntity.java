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
@Table(name="prestamos")
public class PrestamoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_prestamo")
    private Integer idPrestamo;
    @Column(name="id_socio")
    private Integer idSocio;
    @Column(name="id_libro")
    private Integer idLibro;
    @Column(name="fecha_prestamo")
    private String fechaPrestamo;
    @Column(name="fecha_limite")
    private String fechaLimite;
    @Column(name="fecha_devolucion")
    private String fechaDevolucion;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_socio", insertable=false, updatable=false)
    private SocioEntity socio;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_libro", insertable=false, updatable=false)
    private LibroEntity libro;
    
    public PrestamoEntity() {}
    
    public Integer getIdPrestamo() { return idPrestamo; }
    
    public void setIdPrestamo(Integer valor) { this.idPrestamo = valor; }
    
    public Integer getIdSocio() { return idSocio; }
    
    public void setIdSocio(Integer valor) { this.idSocio = valor; }
    
    public Integer getIdLibro() { return idLibro; }
    
    public void setIdLibro(Integer valor) { this.idLibro = valor; }
    
    public String getFechaPrestamo() { return fechaPrestamo; }
    
    public void setFechaPrestamo(String valor) { this.fechaPrestamo = valor; }
    
    public String getFechaLimite() { return fechaLimite; }
    
    public void setFechaLimite(String valor) { this.fechaLimite = valor; }
    
    public String getFechaDevolucion() { return fechaDevolucion; }
    
    public void setFechaDevolucion(String valor) { this.fechaDevolucion = valor; }
}
