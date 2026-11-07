package es.ejemplo.biblioteca;

import es.ejemplo.biblioteca.modelo.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** 12 pruebas CRUD y filtros de AutorDao, cada una con base separada. */
public class AutorDaoTest extends BaseTest {
    @Test
    void insertarAutor() {
        assertTrue(autor() > 0);
    }

    @Test
    void buscarAutor() {
        assertEquals("Autora", autores.buscarPorId(autor()).orElseThrow().getNombre());
    }

    @Test
    void buscarAutorInexistente() {
        assertTrue(autores.buscarPorId(-1).isEmpty());
    }

    @Test
    void listarAutores() {
        autor();
        assertEquals(1, autores.listar().size());
    }

    @Test
    void listarAutoresVacio() {
        assertTrue(autores.listar().isEmpty());
    }

    @Test
    void actualizarAutor() {
        var a = autores.buscarPorId(autor()).orElseThrow();
        a.setNombre("Nueva");
        assertEquals(1, autores.actualizar(a));
    }

    @Test
    void actualizarAutorInexistente() {
        assertEquals(0, autores.actualizar(new Autor(-1, "X", null, null)));
    }

    @Test
    void eliminarAutor() {
        assertEquals(1, autores.eliminar(autor()));
    }

    @Test
    void eliminarAutorInexistente() {
        assertEquals(0, autores.eliminar(-1));
    }

    @Test
    void autorSinNombre() {
        assertEquals(0, autores.insertar(new Autor(null, null, null, null)));
    }

    @Test
    void buscarPorNacionalidad() {
        autor();
        assertEquals(1, autores.buscarPorNacionalidad("España").size());
    }

    @Test
    void contarLibrosPorAutor() {
        int id = autor();
        assertEquals(0, autores.contarLibrosPorAutor(id));
    }
}
