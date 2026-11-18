package es.examen.sportrent.model;

import java.math.BigDecimal;

public class EquipoIngresos {
    private String codigo;
    private String nombre;
    private Long total;
    private BigDecimal ingresos;

    public EquipoIngresos() {
    }

    public EquipoIngresos(String codigo, String nombre, Long total, BigDecimal ingresos) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.total = total;
        this.ingresos = ingresos;
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

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

}
