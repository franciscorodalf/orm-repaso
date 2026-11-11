package es.ejemplo.cinefest.orm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "entradas")
public class EntradaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrada")
    private Integer idEntrada;
    @Column(name = "id_sesion")
    private Integer idSesion;
    @Column(name = "id_espectador")
    private Integer idEspectador;
    @Column(name = "fecha_compra")
    private String fechaCompra;
    @Column(name = "estado")
    private String estado;
    public EntradaEntity() {}
    public Integer getIdEntrada() { return idEntrada; }
    public void setIdEntrada(Integer value) { this.idEntrada = value; }
    public Integer getIdSesion() { return idSesion; }
    public void setIdSesion(Integer value) { this.idSesion = value; }
    public Integer getIdEspectador() { return idEspectador; }
    public void setIdEspectador(Integer value) { this.idEspectador = value; }
    public String getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(String value) { this.fechaCompra = value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }
}
