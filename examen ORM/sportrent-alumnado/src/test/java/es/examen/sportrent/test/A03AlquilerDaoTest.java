
package es.examen.sportrent.test;

import es.examen.sportrent.dao.AlquilerDao;
import es.examen.sportrent.model.Alquiler;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class A03AlquilerDaoTest extends BaseTest {

    private final AlquilerDao dao = new AlquilerDao();

    // =========================================================
    // MÉTODO AUXILIAR PARA CREAR ALQUILERES
    // =========================================================

    private Alquiler nuevo(
            Long id,
            String clienteDni,
            String equipoCodigo,
            String fechaInicio,
            String fechaFin) {

        return new Alquiler(
            id,
            clienteDni,
            equipoCodigo,
            fechaInicio,
            fechaFin,
            null
        );
    }

    // =========================================================
    // 1. LISTADO Y BÚSQUEDA DE ALQUILERES
    // =========================================================

    @Test
    @Order(1)
    void verificarListadoOrdenado() {
        List<Alquiler> alquileres = dao.listarTodos();

        assertEquals(14, alquileres.size());
        assertEquals(1L, alquileres.get(0).getId().longValue());
    }

    @Test
    @Order(2)
    void verificarTipoElementosListado() {
        List<Alquiler> alquileres = dao.listarTodos();

        assertTrue(
            alquileres.stream()
                .allMatch(alquiler -> alquiler.getClass() == Alquiler.class)
        );
    }

    @Test
    @Order(3)
    void verificarBusquedaRegistroExistente() {
        Alquiler alquiler = dao.buscarPorId(1L);

        assertEquals("12345678A", alquiler.getClienteDni());
    }

    @Test
    @Order(4)
    void verificarBusquedaRegistroInexistente() {
        Alquiler alquiler = dao.buscarPorId(99999L);

        assertNull(alquiler);
    }

    // =========================================================
    // 2. INSERCIÓN Y DISPONIBILIDAD
    // =========================================================

    @Test
    @Order(5)
    void verificarInsercionRegistro() {
        Alquiler alquiler = nuevo(
            null,
            "78901234G",
            "KAY-001",
            "2026-11-01",
            "2026-11-03"
        );

        boolean insertado = dao.insertar(alquiler);
        List<Alquiler> alquileres = dao.listarTodos();

        assertTrue(insertado);
        assertEquals(15, alquileres.size());
    }

    @Test
    @Order(6)
    void verificarDisponibilidadEnInsercion() {
        Alquiler alquiler = nuevo(
            null,
            "78901234G",
            "SKI-002",
            "2026-09-10",
            "2026-09-11"
        );

        boolean insertado = assertDoesNotThrow(
            () -> dao.insertar(alquiler)
        );

        assertFalse(insertado);
    }

    // =========================================================
    // 3. ACTUALIZACIÓN DE ALQUILERES
    // =========================================================

    @Test
    @Order(7)
    void verificarActualizacionRegistro() {
        Alquiler modificado = nuevo(
            8L,
            "34567890C",
            "BICI-003",
            "2026-11-01",
            "2026-11-02"
        );

        boolean actualizado = dao.actualizar(modificado);
        Alquiler recuperado = dao.buscarPorId(8L);

        assertTrue(actualizado);
        assertEquals(
            "2026-11-01",
            recuperado.getFechaInicio()
        );
    }

    @Test
    @Order(8)
    void verificarActualizacionRegistroInexistente() {
        Alquiler inexistente = nuevo(
            99999L,
            "34567890C",
            "KAY-001",
            "2026-11-01",
            "2026-11-02"
        );

        boolean actualizado = dao.actualizar(inexistente);

        assertFalse(actualizado);
    }

    // =========================================================
    // 4. ELIMINACIÓN DE ALQUILERES
    // =========================================================

    @Test
    @Order(9)
    void verificarEliminacionRegistro() {
        boolean eliminado = dao.eliminar(8L);
        Alquiler recuperado = dao.buscarPorId(8L);

        assertTrue(eliminado);
        assertNull(recuperado);
    }

    @Test
    @Order(10)
    void verificarEliminacionRegistroInexistente() {
        boolean eliminado = dao.eliminar(99999L);

        assertFalse(eliminado);
    }

    // =========================================================
    // 5. LISTAS VACÍAS Y PARÁMETROS NULOS
    // =========================================================

    @Test
    @Order(11)
    void verificarListadoSinRegistros() {
        sql("DELETE FROM alquileres");

        List<Alquiler> alquileres = dao.listarTodos();

        assertNotNull(alquileres);
        assertTrue(alquileres.isEmpty());
    }

    @Test
    @Order(12)
    void verificarBusquedaConIdentificadorNulo() {
        Alquiler alquiler = assertDoesNotThrow(
            () -> dao.buscarPorId(null)
        );

        assertNull(alquiler);
    }

    @Test
    @Order(13)
    void verificarInsercionConModeloNulo() {
        boolean insertado = assertDoesNotThrow(
            () -> dao.insertar(null)
        );

        assertFalse(insertado);
    }

    @Test
    @Order(14)
    void verificarActualizacionConModeloNulo() {
        boolean actualizado = assertDoesNotThrow(
            () -> dao.actualizar(null)
        );

        assertFalse(actualizado);
    }

    @Test
    @Order(15)
    void verificarEliminacionConIdentificadorNulo() {
        boolean eliminado = assertDoesNotThrow(
            () -> dao.eliminar(null)
        );

        assertFalse(eliminado);
    }

    // =========================================================
    // 6. VALIDACIÓN DE FECHAS Y RELACIONES
    // =========================================================

    @Test
    @Order(16)
    void verificarIntervaloDeFechas() {
        Alquiler alquiler = nuevo(
            null,
            "78901234G",
            "KAY-001",
            "2026-11-05",
            "2026-11-01"
        );

        boolean insertado = dao.insertar(alquiler);

        assertFalse(insertado);
    }

    @Test
    @Order(17)
    void verificarClienteRelacionado() {
        Alquiler alquiler = nuevo(
            null,
            "99999999Z",
            "KAY-001",
            "2026-11-01",
            "2026-11-02"
        );

        boolean insertado = dao.insertar(alquiler);

        assertFalse(insertado);
    }

    @Test
    @Order(18)
    void verificarEquipoRelacionado() {
        Alquiler alquiler = nuevo(
            null,
            "78901234G",
            "ZZ-000",
            "2026-11-01",
            "2026-11-02"
        );

        boolean insertado = dao.insertar(alquiler);

        assertFalse(insertado);
    }

    // =========================================================
    // 7. RELACIONES Y PERSISTENCIA DE DATOS
    // =========================================================

    @Test
    @Order(19)
    void verificarRelacionEquipoDevuelto() {
        Alquiler alquiler = dao.buscarPorId(1L);

        assertEquals("BICI-001", alquiler.getEquipoCodigo());
    }

    @Test
    @Order(20)
    void verificarPersistenciaFechas() {
        Alquiler alquiler = dao.buscarPorId(1L);

        assertEquals(
            "2026-09-03",
            alquiler.getFechaFin()
        );
    }

    // =========================================================
    // 8. INTEGRIDAD Y OPERACIONES RECHAZADAS
    // =========================================================

    @Test
    @Order(21)
    void verificarActualizacionConRelacionInvalida() {
        Alquiler alquiler = nuevo(
            8L,
            "99999999Z",
            "KAY-001",
            "2026-11-01",
            "2026-11-02"
        );

        boolean actualizado = assertDoesNotThrow(
            () -> dao.actualizar(alquiler)
        );

        assertFalse(actualizado);
    }

    @Test
    @Order(22)
    void verificarInsercionConRelacionesInvalidas() {
        Alquiler alquiler = nuevo(
            null,
            "99999999Z",
            "ZZ-000",
            "2026-11-01",
            "2026-11-02"
        );

        boolean insertado = assertDoesNotThrow(
            () -> dao.insertar(alquiler)
        );

        assertFalse(insertado);
    }

    @Test
    @Order(23)
    void verificarIndependenciaEliminacion() {
        boolean eliminado = dao.eliminar(8L);
        Alquiler otroAlquiler = dao.buscarPorId(1L);

        assertTrue(eliminado);
        assertNotNull(otroAlquiler);
    }

    @Test
    @Order(24)
    void verificarListadoEnNuevaSesion() {
        List<Alquiler> alquileres = dao.listarTodos();

        assertTrue(
            alquileres.stream()
                .anyMatch(alquiler -> alquiler.getId() == 14L)
        );
    }

    @Test
    @Order(25)
    void verificarIntegridadTrasInsercionRechazada() {
        Alquiler alquiler = nuevo(
            null,
            "78901234G",
            "SKI-002",
            "2026-09-10",
            "2026-09-11"
        );

        boolean insertado = dao.insertar(alquiler);
        List<Alquiler> alquileres = dao.listarTodos();

        assertFalse(insertado);
        assertEquals(14, alquileres.size());
    }
}
