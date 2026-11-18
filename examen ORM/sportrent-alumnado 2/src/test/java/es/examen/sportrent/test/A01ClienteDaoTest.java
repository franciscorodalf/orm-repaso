
package es.examen.sportrent.test;

import es.examen.sportrent.dao.ClienteDao;
import es.examen.sportrent.model.Cliente;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class A01ClienteDaoTest extends BaseTest {

    private final ClienteDao dao = new ClienteDao();

    // =========================================================
    // 1. CONSULTAS BÁSICAS DEL CRUD
    // =========================================================

    @Test
    @Order(1)
    void verificarListadoOrdenado() {
        List<Cliente> clientes = dao.listarTodos();

        assertEquals(7, clientes.size());
        assertEquals("12345678A", clientes.get(0).getDni());
    }

    @Test
    @Order(2)
    void verificarTipoElementosListado() {
        List<Cliente> clientes = dao.listarTodos();

        assertTrue(
            clientes.stream()
                .allMatch(cliente -> cliente.getClass() == Cliente.class)
        );
    }

    @Test
    @Order(3)
    void verificarBusquedaRegistroExistente() {
        Cliente cliente = dao.buscarPorId("12345678A");

        assertEquals("Ana López", cliente.getNombre());
    }

    @Test
    @Order(4)
    void verificarBusquedaRegistroInexistente() {
        Cliente cliente = dao.buscarPorId("99999999Z");

        assertNull(cliente);
    }

    // =========================================================
    // 2. INSERCIÓN DE REGISTROS
    // =========================================================

    @Test
    @Order(5)
    void verificarInsercionRegistro() {
        Cliente nuevo = new Cliente(
            "99999999Z",
            "Nueva",
            "nueva@test.es",
            "600000000"
        );

        boolean insertado = dao.insertar(nuevo);
        Cliente recuperado = dao.buscarPorId("99999999Z");

        assertTrue(insertado);
        assertNotNull(recuperado);
    }

    @Test
    @Order(6)
    void verificarInsercionRegistroDuplicado() {
        Cliente duplicado = new Cliente(
            "12345678A",
            "X",
            "x@test.es",
            "123"
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
        Cliente modificado = new Cliente(
            "12345678A",
            "Ana Editada",
            "ana-editada@test.es",
            "611111111"
        );

        boolean actualizado = dao.actualizar(modificado);
        Cliente recuperado = dao.buscarPorId("12345678A");

        assertTrue(actualizado);
        assertEquals("Ana Editada", recuperado.getNombre());
    }

    @Test
    @Order(8)
    void verificarActualizacionRegistroInexistente() {
        Cliente inexistente = new Cliente(
            "99999999Z",
            "X",
            "x@test.es",
            "123"
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
        boolean eliminado = dao.eliminar("78901234G");
        Cliente recuperado = dao.buscarPorId("78901234G");

        assertTrue(eliminado);
        assertNull(recuperado);
    }

    @Test
    @Order(10)
    void verificarEliminacionRegistroInexistente() {
        boolean eliminado = dao.eliminar("99999999Z");

        assertFalse(eliminado);
    }

    // =========================================================
    // 5. LISTAS VACÍAS Y PARÁMETROS NULOS
    // =========================================================

    @Test
    @Order(11)
    void verificarListadoSinRegistros() {
        sql("DELETE FROM alquileres");
        sql("DELETE FROM clientes");

        List<Cliente> clientes = dao.listarTodos();

        assertNotNull(clientes);
        assertTrue(clientes.isEmpty());
    }

    @Test
    @Order(12)
    void verificarBusquedaConIdentificadorNulo() {
        Cliente cliente = assertDoesNotThrow(
            () -> dao.buscarPorId(null)
        );

        assertNull(cliente);
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
        Cliente cliente = dao.buscarPorId("");
        boolean eliminado = dao.eliminar(" ");

        assertNull(cliente);
        assertFalse(eliminado);
    }

    @Test
    @Order(17)
    void verificarIdentificadorEnInsercion() {
        Cliente cliente = new Cliente(
            " ",
            "Nombre",
            "a@test.es",
            "123"
        );

        boolean insertado = dao.insertar(cliente);

        assertFalse(insertado);
    }

    @Test
    @Order(18)
    void verificarValidacionAtributos() {
        Cliente cliente = new Cliente(
            "X",
            "",
            "a@test.es",
            "123"
        );

        boolean insertado = dao.insertar(cliente);

        assertFalse(insertado);
    }

    // =========================================================
    // 7. INTEGRIDAD Y CONSERVACIÓN DE DATOS
    // =========================================================

    @Test
    @Order(19)
    void verificarIntegridadReferencial() {
        boolean eliminado = assertDoesNotThrow(
            () -> dao.eliminar("12345678A")
        );

        Cliente cliente = dao.buscarPorId("12345678A");

        assertFalse(eliminado);
        assertNotNull(cliente);
    }

    @Test
    @Order(20)
    void verificarConservacionDatosTrasDuplicado() {
        Cliente duplicado = new Cliente(
            "12345678A",
            "Sobrescrito",
            "x@test.es",
            "123"
        );

        boolean insertado = dao.insertar(duplicado);
        Cliente original = dao.buscarPorId("12345678A");

        assertFalse(insertado);
        assertEquals("Ana López", original.getNombre());
    }
}
