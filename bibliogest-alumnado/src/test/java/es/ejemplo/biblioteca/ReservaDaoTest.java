package es.ejemplo.biblioteca;

import es.ejemplo.biblioteca.modelo.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReservaDaoTest extends BaseTest {
    @Test
    void insertarReserva() {
        assertTrue(reserva() > 0);
    }

    @Test
    void buscarReserva() {
        assertTrue(reservas.buscarPorId(reserva()).isPresent());
    }

    @Test
    void buscarReservaInexistente() {
        assertTrue(reservas.buscarPorId(-1).isEmpty());
    }

    @Test
    void listarReservas() {
        reserva();
        assertEquals(1, reservas.listar().size());
    }

    @Test
    void listarReservasVacio() {
        assertTrue(reservas.listar().isEmpty());
    }

    @Test
    void actualizarReserva() {
        int id = reserva();
        var r = reservas.buscarPorId(id).orElseThrow();
        r.setEstado("ATENDIDA");
        assertEquals(1, reservas.actualizar(r));
    }

    @Test
    void actualizarReservaInexistente() {
        assertEquals(0, reservas.actualizar(new Reserva(-1, socio(), libro(), "2025-01-01", "PENDIENTE")));
    }

    @Test
    void eliminarReserva() {
        assertEquals(1, reservas.eliminar(reserva()));
    }

    @Test
    void eliminarReservaInexistente() {
        assertEquals(0, reservas.eliminar(-1));
    }

    @Test
    void listarPendientes() {
        reserva();
        assertEquals(1, reservas.listarPendientes().size());
    }

    @Test
    void buscarPorSocio() {
        int s = socio();
        reservas.insertar(new Reserva(null, s, libro(), "2025-01-01", "PENDIENTE"));
        assertEquals(1, reservas.buscarPorSocio(s).size());
    }

    @Test
    void listarPorLibro() {
        int l = libro();
        reservas.insertar(new Reserva(null, socio(), l, "2025-01-01", "PENDIENTE"));
        assertEquals(1, reservas.listarPorLibro(l).size());
    }
}
