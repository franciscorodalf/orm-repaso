package es.ejemplo.biblioteca;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class ConsultasAvanzadasTest extends BaseTest {
    @Test
    void librosNuncaPrestados() {
        libro();
        assertEquals(1, libros.nuncaPrestados().size());
    }

    @Test
    void sociosPrestamoVencido() {
        prestamo();
        assertEquals(1, socios.listarConPrestamosVencidos("2025-03-01").size());
    }

    @Test
    void sobreMediaPrestamos() {
        libro();
        assertNotNull(libros.masPrestadosQueMedia());
    }

    @Test
    void autoresTresSocios() {
        autor();
        assertTrue(autores.autoresLeidosPorTresSocios().isEmpty());
    }

    @Test
    void reservasSinDisponibilidad() {
        reserva();
        assertTrue(libros.reservadosSinDisponibilidad().isEmpty());
    }
}
