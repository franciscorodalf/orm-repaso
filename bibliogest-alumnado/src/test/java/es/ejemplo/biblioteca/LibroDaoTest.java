package es.ejemplo.biblioteca;

import es.ejemplo.biblioteca.modelo.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** 12 pruebas CRUD y filtros de LibroDao, cada una con base separada. */
public class LibroDaoTest extends BaseTest {
    @Test
    void insertarLibro() {
        assertTrue(libro() > 0);
    }

    @Test
    void buscarLibro() {
        assertEquals("Libro prueba", libros.buscarPorId(libro()).orElseThrow().getTitulo());
    }

    @Test
    void buscarLibroInexistente() {
        assertTrue(libros.buscarPorId(-1).isEmpty());
    }

    @Test
    void listarLibros() {
        libro();
        assertEquals(1, libros.listar().size());
    }

    @Test
    void listarLibrosVacio() {
        assertTrue(libros.listar().isEmpty());
    }

    @Test
    void actualizarLibro() {
        var l = libros.buscarPorId(libro()).orElseThrow();
        l.setTitulo("Nuevo");
        assertEquals(1, libros.actualizar(l));
    }

    @Test
    void actualizarLibroInexistente() {
        assertEquals(0, libros.actualizar(new Libro(-1, "X", "X", "G", 2020, 1, autor())));
    }

    @Test
    void eliminarLibro() {
        assertEquals(1, libros.eliminar(libro()));
    }

    @Test
    void isbnDuplicado() {
        libro();
        assertEquals(0, libros.insertar(new Libro(null, "B", "ISBN-001", "Novela", 2020, 1, autor())));
    }

    @Test
    void buscarPorGenero() {
        libro();
        assertEquals(1, libros.buscarPorGenero("Novela").size());
    }

    @Test
    void buscarPorAutor() {
        int id = autor();
        libros.insertar(new Libro(null, "B", "ISBN-1", "Novela", 2020, 1, id));
        assertEquals(1, libros.buscarPorAutor(id).size());
    }

    @Test
    void listarDisponibles() {
        libro();
        assertEquals(1, libros.listarDisponibles().size());
    }
}
