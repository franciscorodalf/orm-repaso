package es.ejemplo.cinefest.modelo;

public class Espectador {
    private Integer idEspectador;
    private String nombre;
    private String email;
    private String fechaAlta;
    private Integer activo;
    public Espectador() {}
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
