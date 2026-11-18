package es.examen.sportrent.model;

import java.math.BigDecimal;

/** Resultado de consulta del dominio SportRent. */
public class ClienteAlquileres {
    private String dni;
    private String nombre;
    private Long total;

    public ClienteAlquileres() {
    }

    public ClienteAlquileres(String dni, String nombre, Long total) {
        this.dni = dni;
        this.nombre = nombre;
        this.total = total;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
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
