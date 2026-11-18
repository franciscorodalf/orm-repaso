package es.examen.sportrent.model;

public class Alquiler {
    private Long id;
    private String clienteDni;
    private String equipoCodigo;
    private String fechaInicio;
    private String fechaFin;
    private String fechaDevolucion;
    public Alquiler() { }
    public Alquiler(Long id, String clienteDni, String equipoCodigo, String fechaInicio, String fechaFin, String fechaDevolucion) {
        this.id = id;
        this.clienteDni = clienteDni;
        this.equipoCodigo = equipoCodigo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaDevolucion = fechaDevolucion;
    }
    public Long getId() { return id; }
    public String getClienteDni() { return clienteDni; }
    public String getEquipoCodigo() { return equipoCodigo; }
    public String getFechaInicio() { return fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public String getFechaDevolucion() { return fechaDevolucion; }
    public void setId(Long id) {
        this.id = id;
    }
    public void setClienteDni(String clienteDni) {
        this.clienteDni = clienteDni;
    }
    public void setEquipoCodigo(String equipoCodigo) {
        this.equipoCodigo = equipoCodigo;
    }
    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }
    public void setFechaDevolucion(String fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }
}