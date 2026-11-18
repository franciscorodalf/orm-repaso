package es.examen.sportrent.model;

import java.math.BigDecimal;

public class PrecioCategoria {
    private String categoria;
    private BigDecimal precioMaximo;

    public PrecioCategoria() {
    }

    public PrecioCategoria(String categoria, BigDecimal precioMaximo) {
        this.categoria = categoria;
        this.precioMaximo = precioMaximo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecioMaximo() {
        return precioMaximo;
    }

    public void setPrecioMaximo(BigDecimal precioMaximo) {
        this.precioMaximo = precioMaximo;
    }

}
