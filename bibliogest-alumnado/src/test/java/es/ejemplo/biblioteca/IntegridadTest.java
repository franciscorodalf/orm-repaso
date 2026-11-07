package es.ejemplo.biblioteca;

import es.ejemplo.biblioteca.modelo.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntegridadTest extends BaseTest {
    @Test
    void emailDuplicadoRechazado() {
        socio();
        assertEquals(0, socios.insertar(new Socio(null, "B", "ana@prueba.test", null, "2025-01-01", true)));
    }

    @Test
    void isbnDuplicadoRechazado() {
        libro();
        assertEquals(0, libros.insertar(new Libro(null, "B", "ISBN-001", "G", 2020, 1, autor())));
    }

    @Test
    void autorInexistente() {
        assertEquals(0, libros.insertar(new Libro(null, "B", "ISBN-2", "G", 2020, 1, -1)));
    }

    @Test
    void socioInexistenteEnPrestamo() {
        assertEquals(0, prestamos.registrarPrestamo(-1, libro(), "2025-01-01", "2025-02-01"));
    }

    @Test
    void libroInexistenteEnReserva() {
        assertEquals(0, reservas.insertar(new Reserva(null, socio(), -1, "2025-01-01", "PENDIENTE")));
    }

    @Test
    void eliminarAutorConLibros() {
        int id = autor();
        libros.insertar(new Libro(null, "B", "ISBN-3", "G", 2020, 1, id));
        assertEquals(0, autores.eliminar(id));
    }

    @Test
    void eliminarSocioConPrestamos() {
        int id = socio();
        prestamos.registrarPrestamo(id, libro(), "2025-01-01", "2025-02-01");
        assertEquals(0, socios.eliminar(id));
    }

    @Test
    void ejemplaresNegativos() {
        assertEquals(0, libros.insertar(new Libro(null, "B", "ISBN-4", "G", 2020, -1, autor())));
    }

    @Test
    void estadoReservaIncorrecto() {
        assertEquals(0, reservas.insertar(new Reserva(null, socio(), libro(), "2025-01-01", "X")));
    }

    @Test
    void fechaDevolucionAnterior() {
        assertEquals(0,
                prestamos.insertar(new Prestamo(null, socio(), libro(), "2025-02-01", "2025-02-15", "2025-01-01")));
    }
}
