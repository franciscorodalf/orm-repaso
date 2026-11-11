package es.ejemplo.cinefest.modelo;

public class Sala {
    private Integer idSala;
    private String nombre;
    private Integer aforo;
    private Integer idSede;
    public Sala() {}
    public Integer getIdSala() { return idSala; }
    public void setIdSala(Integer value) { this.idSala = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public Integer getAforo() { return aforo; }
    public void setAforo(Integer value) { this.aforo = value; }
    public Integer getIdSede() { return idSede; }
    public void setIdSede(Integer value) { this.idSede = value; }
}
