package es.ejemplo.biblioteca;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConsultasJoinTest extends BaseTest {
    @Test
    void libroConAutor() {
        libro();
        assertEquals(1, libros.librosConAutor().size());
    }

    @Test
    void prestamoConSocioYLibro() {
        prestamo();
        assertEquals(1, prestamos.prestamosConSocioYLibro().size());
    }

    @Test
    void prestamosSocio() {
        int s = socio();
        prestamos.registrarPrestamo(s, libro(), "2025-01-01", "2025-02-01");
        assertEquals(1, prestamos.listarPorSocio(s).size());
    }

    @Test
    void sociosSinPrestamos() {
        socio();
        assertEquals(1, socios.sociosSinPrestamos().size());
    }

    @Test
    void reservasConDetalles() {
        reserva();
        assertEquals(1, reservas.reservasConDetalles().size());
    }
}
