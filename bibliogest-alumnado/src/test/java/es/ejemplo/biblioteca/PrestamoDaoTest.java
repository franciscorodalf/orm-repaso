package es.ejemplo.biblioteca;

import es.ejemplo.biblioteca.modelo.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PrestamoDaoTest extends BaseTest {
    @Test
    void insertarPrestamo() {
        assertTrue(prestamo() > 0);
    }

    @Test
    void buscarPrestamo() {
        assertTrue(prestamos.buscarPorId(prestamo()).isPresent());
    }

    @Test
    void buscarPrestamoInexistente() {
        assertTrue(prestamos.buscarPorId(-1).isEmpty());
    }

    @Test
    void listarPrestamos() {
        prestamo();
        assertEquals(1, prestamos.listar().size());
    }

    @Test
    void listarPrestamosVacio() {
        assertTrue(prestamos.listar().isEmpty());
    }

    @Test
    void actualizarPrestamo() {
        int id = prestamo();
        var p = prestamos.buscarPorId(id).orElseThrow();
        p.setFechaDevolucion("2025-01-15");
        assertEquals(1, prestamos.actualizar(p));
    }

    @Test
    void actualizarPrestamoInexistente() {
        assertEquals(0, prestamos.actualizar(new Prestamo(-1, socio(), libro(), "2025-01-01", "2025-02-01", null)));
    }

    @Test
    void eliminarPrestamo() {
        assertEquals(1, prestamos.eliminar(prestamo()));
    }

    @Test
    void eliminarPrestamoInexistente() {
        assertEquals(0, prestamos.eliminar(-1));
    }

    @Test
    void listarPorSocio() {
        int s = socio();
        prestamos.registrarPrestamo(s, libro(), "2025-01-01", "2025-02-01");
        assertEquals(1, prestamos.listarPorSocio(s).size());
    }

    @Test
    void listarActivos() {
        prestamo();
        assertEquals(1, prestamos.listarActivos().size());
    }

    @Test
    void listarVencidos() {
        prestamo();
        assertEquals(1, prestamos.listarVencidos("2025-03-01").size());
    }
}
