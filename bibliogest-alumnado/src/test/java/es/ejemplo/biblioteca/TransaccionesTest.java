package es.ejemplo.biblioteca;

import es.ejemplo.biblioteca.modelo.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransaccionesTest extends BaseTest {
    @Test
    void prestarLibroDisponible() {
        assertTrue(prestamo() > 0);
    }

    @Test
    void prestarSinDisponibilidad()  {
        int s = socio(), l = libro();
        assertTrue(prestamos.registrarPrestamo(s, l, "2025-01-01", "2025-02-01") > 0);
        assertTrue(prestamos.registrarPrestamo(s, l, "2025-01-02", "2025-02-02") > 0);
        assertEquals(0, prestamos.registrarPrestamo(s, l, "2025-01-03", "2025-02-03"));
    }

    @Test
    void prestarSocioInactivo()  {
        int s = socios.insertar(new Socio(null, "Inactivo", "i@x.test", null, "2025-01-01", false));
        assertEquals(0, prestamos.registrarPrestamo(s, libro(), "2025-01-01", "2025-02-01"));
    }

    @Test
    void rollbackAnteError()  {
        int l = libro();
        assertEquals(0, prestamos.registrarPrestamo(-1, l, "2025-01-01", "2025-02-01"));
        assertTrue(prestamos.listar().isEmpty());
    }

    @Test
    void prestamosConcurrentes() throws Exception {
        int s = socio(), l = libros.insertar(new Libro(null, "Solo uno", "ISBN-5", "Novela", 2020, 1, autor()));
        java.util.concurrent.CountDownLatch start = new java.util.concurrent.CountDownLatch(1);
        java.util.concurrent.atomic.AtomicInteger ok = new java.util.concurrent.atomic.AtomicInteger();
        java.util.List<Thread> threads = new java.util.ArrayList<>();
        for (int i = 0; i < 2; i++) {
            final int day = i + 1;
            Thread t = new Thread(() -> {
                try {
                    start.await();
                    if (prestamos.registrarPrestamo(s, l, "2025-01-0" + day, "2025-02-01") > 0)
                        ok.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads.add(t);
            t.start();
        }
        start.countDown();
        for (Thread t : threads)
            t.join();
        assertEquals(1, ok.get());
    }
}
