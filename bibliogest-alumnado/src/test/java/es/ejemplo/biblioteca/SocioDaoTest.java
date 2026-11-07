package es.ejemplo.biblioteca;

import es.ejemplo.biblioteca.modelo.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class SocioDaoTest extends BaseTest {
    @Test
    void insertarSocioCorrecto() {
        assertTrue(socio() > 0);
    }

    @Test
    void buscarSocioExistente() {
        int id = socio();
        assertEquals("Ana", socios.buscarPorId(id).orElseThrow().getNombre());
    }

    @Test
    void buscarSocioInexistente() {
        assertTrue(socios.buscarPorId(-100).isEmpty());
    }

    @Test
    void listarSocios() {
        socio();
        assertEquals(1, socios.listar().size());
    }

    @Test
    void listarSinRegistros() {
        assertTrue(socios.listar().isEmpty());
    }

    @Test
    void actualizarSocio() {
        int id = socio();
        var s = socios.buscarPorId(id).orElseThrow();
        s.setNombre("Bea");
        assertEquals(1, socios.actualizar(s));
        assertEquals("Bea", socios.buscarPorId(id).orElseThrow().getNombre());
    }

    @Test
    void actualizarInexistente() {
        assertEquals(0, socios.actualizar(new Socio(-22, "Ana", "x@t.test", null, "2025-01-01", true)));
    }

    @Test
    void eliminarSocio() {
        int id = socio();
        assertEquals(1, socios.eliminar(id));
        assertTrue(socios.buscarPorId(id).isEmpty());
    }

    @Test
    void eliminarInexistente() {
        assertEquals(0, socios.eliminar(-20));
    }

    @Test
    void emailDuplicado() {
        socio();
        assertEquals(0, socios.insertar(new Socio(null, "Otra", "ana@prueba.test", null, "2025-01-01", true)));
    }

    @Test
    void buscarPorEmail() {
        socio();
        assertTrue(socios.buscarPorEmail("ana@prueba.test").isPresent());
    }

    @Test
    void listarActivos() {
        socio();
        socios.insertar(new Socio(null, "Inactiva", "inactiva@test.com", null, "2025-01-01", false));
        assertEquals(1, socios.listarActivos().size());
    }
}
