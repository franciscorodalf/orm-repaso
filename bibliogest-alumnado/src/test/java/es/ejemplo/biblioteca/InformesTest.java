package es.ejemplo.biblioteca;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de consulta de datos relacionales. */
public class InformesTest extends BaseTest {
    @Test
    void rankingMensual() {
        prestamo();
        assertEquals(1, prestamos.rankingMensual("2025-01", 3).size());
    }

    @Test
    void historial() {
        int s = socio();
        prestamos.registrarPrestamo(s, libro(), "2025-01-01", "2025-02-01");
        assertEquals(1, socios.historialSocio(s).size());
    }

    @Test
    void prioridadReservas() {
        reserva();
        assertEquals(1, reservas.prioridadReservas().size());
    }

    @Test
    void estadisticasGenero() {
        libro();
        assertEquals(1, libros.estadisticasGenero().size());
    }

    @Test
    void registrarPrestamoCompleto() {
        assertTrue(prestamo() > 0);
    }
}
