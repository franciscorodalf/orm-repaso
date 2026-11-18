package es.examen.sportrent.entity;
import jakarta.persistence.*;

public class AlquilerEntity {

    private Long id;
  
    private String fechaInicio;

    private String fechaFin;

    private String fechaDevolucion;
  
    private java.math.BigDecimal importeTotal;

}
