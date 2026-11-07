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
@Table(name="reservas")
public class ReservaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_reserva")
    private Integer idReserva;
    @Column(name="id_socio")
    private Integer idSocio;
    @Column(name="id_libro")
    private Integer idLibro;
    @Column(name="fecha_reserva")
    private String fechaReserva;
    @Column(name="estado")
    private String estado;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_socio", insertable=false, updatable=false)
    private SocioEntity socio;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_libro", insertable=false, updatable=false)
    private LibroEntity libro;
    
    public ReservaEntity() {}
    
    public Integer getIdReserva() { return idReserva; }
    
    public void setIdReserva(Integer valor) { this.idReserva = valor; }
    
    public Integer getIdSocio() { return idSocio; }
    
    public void setIdSocio(Integer valor) { this.idSocio = valor; }
    
    public Integer getIdLibro() { return idLibro; }
    
    public void setIdLibro(Integer valor) { this.idLibro = valor; }
    
    public String getFechaReserva() { return fechaReserva; }
    
    public void setFechaReserva(String valor) { this.fechaReserva = valor; }
    
    public String getEstado() { return estado; }
    
    public void setEstado(String valor) { this.estado = valor; }
}
