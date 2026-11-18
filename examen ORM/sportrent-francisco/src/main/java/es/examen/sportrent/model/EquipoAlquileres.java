package es.examen.sportrent.model;



public class EquipoAlquileres {
    private String codigo;
    private String nombre;
    private Long total;

    public EquipoAlquileres() {
    }

    public EquipoAlquileres(String codigo, String nombre, Long total) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.total = total;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

}
