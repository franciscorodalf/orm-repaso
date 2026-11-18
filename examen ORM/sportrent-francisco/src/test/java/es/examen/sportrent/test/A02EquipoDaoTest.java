
package es.examen.sportrent.test;

import es.examen.sportrent.dao.EquipoDao;
import es.examen.sportrent.model.Equipo;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class A02EquipoDaoTest extends BaseTest {

    private final EquipoDao dao = new EquipoDao();

    // =========================================================
    // 1. CONSULTAS BÁSICAS DEL CRUD
    // =========================================================

    @Test
    @Order(1)
    void verificarListadoOrdenado() {
        List<Equipo> equipos = dao.listarTodos();

        assertEquals(9, equipos.size());
        assertEquals("BICI-001", equipos.get(0).getCodigo());
    }

    @Test
    @Order(2)
    void verificarTipoElementosListado() {
        List<Equipo> equipos = dao.listarTodos();

        assertTrue(
            equipos.stream()
                .allMatch(equipo -> equipo.getClass() == Equipo.class)
        );
    }

    @Test
    @Order(3)
    void verificarBusquedaRegistroExistente() {
        Equipo equipo = dao.buscarPorId("BICI-001");

        assertEquals("Bicicleta urbana", equipo.getNombre());
    }

    @Test
    @Order(4)
    void verificarBusquedaRegistroInexistente() {
        Equipo equipo = dao.buscarPorId("X-999");

        assertNull(equipo);
    }

    // =========================================================
    // 2. INSERCIÓN DE REGISTROS
    // =========================================================

    @Test
    @Order(5)
    void verificarInsercionRegistro() {
        Equipo nuevo = new Equipo(
            "NEW-999",
            "Nuevo",
            "Fitness",
            new BigDecimal("19.00"),
            "DISPONIBLE"
        );

        boolean insertado = dao.insertar(nuevo);
        Equipo recuperado = dao.buscarPorId("NEW-999");

        assertTrue(insertado);
        assertNotNull(recuperado);
    }

    @Test
    @Order(6)
    void verificarInsercionRegistroDuplicado() {
        Equipo duplicado = new Equipo(
            "BICI-001",
            "Duplicado",
            "Ciclismo",
            new BigDecimal("9"),
            "DISPONIBLE"
        );

        boolean insertado = assertDoesNotThrow(
            () -> dao.insertar(duplicado)
        );

        assertFalse(insertado);
    }

    // =========================================================
    // 3. ACTUALIZACIÓN DE REGISTROS
    // =========================================================

    @Test
    @Order(7)
    void verificarActualizacionRegistro() {
        Equipo modificado = new Equipo(
            "BICI-001",
            "Urbana v2",
            "Ciclismo",
            new BigDecimal("16"),
            "DISPONIBLE"
        );

        boolean actualizado = dao.actualizar(modificado);
        Equipo recuperado = dao.buscarPorId("BICI-001");

        assertTrue(actualizado);
        assertEquals("Urbana v2", recuperado.getNombre());
    }

    @Test
    @Order(8)
    void verificarActualizacionRegistroInexistente() {
        Equipo inexistente = new Equipo(
            "X-999",
            "X",
            "Surf",
            new BigDecimal("12"),
            "DISPONIBLE"
        );

        boolean actualizado = dao.actualizar(inexistente);

        assertFalse(actualizado);
    }

    // =========================================================
    // 4. ELIMINACIÓN DE REGISTROS
    // =========================================================

    @Test
    @Order(9)
    void verificarEliminacionRegistro() {
        boolean eliminado = dao.eliminar("KAY-001");
        Equipo recuperado = dao.buscarPorId("KAY-001");

        assertTrue(eliminado);
        assertNull(recuperado);
    }

    @Test
    @Order(10)
    void verificarEliminacionRegistroInexistente() {
        boolean eliminado = dao.eliminar("X-999");

        assertFalse(eliminado);
    }

    // =========================================================
    // 5. LISTAS VACÍAS Y PARÁMETROS NULOS
    // =========================================================

    @Test
    @Order(11)
    void verificarListadoSinRegistros() {
        sql("DELETE FROM alquileres");
        sql("DELETE FROM equipos");

        List<Equipo> equipos = dao.listarTodos();

        assertNotNull(equipos);
        assertTrue(equipos.isEmpty());
    }

    @Test
    @Order(12)
    void verificarBusquedaConIdentificadorNulo() {
        Equipo equipo = assertDoesNotThrow(
            () -> dao.buscarPorId(null)
        );

        assertNull(equipo);
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
    // 6. VALIDACIÓN DE IDENTIFICADORES Y ATRIBUTOS
    // =========================================================

    @Test
    @Order(16)
    void verificarIdentificadorVacio() {
        Equipo equipo = dao.buscarPorId("");
        boolean eliminado = dao.eliminar(" ");

        assertNull(equipo);
        assertFalse(eliminado);
    }

    @Test
    @Order(17)
    void verificarIdentificadorEnInsercion() {
        Equipo equipo = new Equipo(
            " ",
            "Nuevo",
            "Surf",
            new BigDecimal("12"),
            "DISPONIBLE"
        );

        boolean insertado = dao.insertar(equipo);

        assertFalse(insertado);
    }

    @Test
    @Order(18)
    void verificarValidacionAtributos() {
        Equipo equipo = new Equipo(
            "X-998",
            "Nuevo",
            "Surf",
            BigDecimal.ZERO,
            "DISPONIBLE"
        );

        boolean insertado = dao.insertar(equipo);

        assertFalse(insertado);
    }

    // =========================================================
    // 7. INTEGRIDAD Y CONSERVACIÓN DE DATOS
    // =========================================================

    @Test
    @Order(19)
    void verificarIntegridadReferencial() {
        boolean eliminado = assertDoesNotThrow(
            () -> dao.eliminar("BICI-001")
        );

        Equipo equipo = dao.buscarPorId("BICI-001");

        assertFalse(eliminado);
        assertNotNull(equipo);
    }

    @Test
    @Order(20)
    void verificarConservacionDatosTrasDuplicado() {
        Equipo duplicado = new Equipo(
            "BICI-001",
            "Sobrescrito",
            "Surf",
            new BigDecimal("99"),
            "DISPONIBLE"
        );

        boolean insertado = dao.insertar(duplicado);
        Equipo original = dao.buscarPorId("BICI-001");

        assertFalse(insertado);
        assertEquals("Bicicleta urbana", original.getNombre());
    }
}
