package es.ejemplo.biblioteca;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de consulta de datos relacionales. */
public class ConsultasAgrupacionTest extends BaseTest {
    @Test
    void conteoGenero() {
        libro();
        assertEquals(1, libros.contarPorGenero().size());
    }

    @Test
    void conteoAutoresSinLibros() {
        autor();
        assertEquals(1, autores.autoresConRecuento().size());
    }

    @Test
    void rankingSocios() {
        prestamo();
        assertEquals(1, socios.rankingSocios(5).size());
    }

    @Test
    void havingGenero() {
        prestamo();
        assertEquals(1, libros.prestamosPorGeneroMinimo(0).size());
    }

    @Test
    void disponibilidadAgrupada() {
        libro();
        assertEquals(1, libros.disponibilidadPorLibro().size());
    }
}
