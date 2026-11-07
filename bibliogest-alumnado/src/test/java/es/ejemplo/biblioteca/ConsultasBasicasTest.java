package es.ejemplo.biblioteca;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class ConsultasBasicasTest extends BaseTest {
    @Test
    void ordenaLibros() {
        libro();
        assertEquals(1, libros.listar().size());
    }

    @Test
    void filtraSociosActivos() {
        socio();
        assertEquals(1, socios.listarActivos().size());
    }

    @Test
    void busquedaLike() {
        libro();
        assertEquals(1, libros.buscarPorTitulo("prueba").size());
    }

    @Test
    void busquedaDesdeAnio() {
        libro();
        assertEquals(1, libros.librosDesdeAnio(2015, "Novela").size());
    }

    @Test
    void generosDistintos() {
        libro();
        assertEquals(java.util.List.of("Novela"), libros.generos());
    }
}
