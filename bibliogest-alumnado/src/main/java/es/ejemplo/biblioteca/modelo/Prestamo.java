package es.ejemplo.biblioteca.modelo;

public class Prestamo {
    private Integer idPrestamo;
    private Integer idSocio;
    private Integer idLibro;
    private String fechaPrestamo;
    private String fechaLimite;
    private String fechaDevolucion;
    
    public Prestamo() {}
    
    public Prestamo(Integer idPrestamo, Integer idSocio, Integer idLibro, String fechaPrestamo, String fechaLimite, String fechaDevolucion) {
        this.idPrestamo = idPrestamo;
        this.idSocio = idSocio;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaLimite = fechaLimite;
        this.fechaDevolucion = fechaDevolucion;
    }
    
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
