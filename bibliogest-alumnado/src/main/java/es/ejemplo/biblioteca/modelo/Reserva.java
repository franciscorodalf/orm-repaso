package es.ejemplo.biblioteca.modelo;

public class Reserva {
    private Integer idReserva;
    private Integer idSocio;
    private Integer idLibro;
    private String fechaReserva;
    private String estado;
    
    public Reserva() {}
    
    public Reserva(Integer idReserva, Integer idSocio, Integer idLibro, String fechaReserva, String estado) {
        this.idReserva = idReserva;
        this.idSocio = idSocio;
        this.idLibro = idLibro;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
    }
    
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
