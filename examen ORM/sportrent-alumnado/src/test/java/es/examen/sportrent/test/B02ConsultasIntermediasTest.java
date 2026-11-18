
package es.examen.sportrent.test;

import es.examen.sportrent.dao.AlquilerDao;
import es.examen.sportrent.dao.ClienteDao;
import es.examen.sportrent.dao.EquipoDao;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class B02ConsultasIntermediasTest extends BaseTest {

    private final ClienteDao cliente = new ClienteDao();
    private final EquipoDao equipo = new EquipoDao();
    private final AlquilerDao alquiler = new AlquilerDao();

    // =========================================================
    // 1. CLIENTES CON MÁS DE DOS ALQUILERES
    // =========================================================

    @Test
    @Order(1)
    void verificarResultadoClientesConMasDeDosAlquileres() {
        var resultado = cliente.clientesConMasDeDosAlquileres();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    @Order(2)
    void verificarOrdenacionClientesConMasDeDosAlquileres() {
        var resultado = cliente.clientesConMasDeDosAlquileres();

        assertFalse(resultado.isEmpty());
        assertEquals("12345678A", resultado.get(0).getDni());
    }

    @Test
    @Order(3)
    void verificarImplementacionClientesConMasDeDosAlquileres() {
        cliente.clientesConMasDeDosAlquileres();

        estructura("having", "group by", "count(");
    }

    @Test
    @Order(4)
    void verificarResultadoVacioClientesConMasDeDosAlquileres() {
        sql("DELETE FROM alquileres");

        var resultado = assertDoesNotThrow(
            () -> cliente.clientesConMasDeDosAlquileres()
        );

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @Order(5)
    void verificarCantidadPrimerClienteClientesConMasDeDosAlquileres() {
        var resultado = cliente.clientesConMasDeDosAlquileres();

        assertEquals(
            4L,
            resultado.get(0).getTotal().longValue()
        );
    }

    // =========================================================
    // 2. PRECIO MÁXIMO POR CATEGORÍA
    // =========================================================

    @Test
    @Order(6)
    void verificarResultadoMaximoPrecioPorCategoria() {
        var resultado = equipo.maximoPrecioPorCategoria();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
    }

    @Test
    @Order(7)
    void verificarOrdenacionMaximoPrecioPorCategoria() {
        var resultado = equipo.maximoPrecioPorCategoria();

        assertFalse(resultado.isEmpty());
        assertEquals("Ciclismo", resultado.get(0).getCategoria());
    }

    @Test
    @Order(8)
    void verificarImplementacionMaximoPrecioPorCategoria() {
        equipo.maximoPrecioPorCategoria();

        estructura("having", "max(", "group by");
    }

    @Test
    @Order(9)
    void verificarResultadoVacioMaximoPrecioPorCategoria() {
        sql("DELETE FROM alquileres");
        sql("DELETE FROM equipos");

        var resultado = assertDoesNotThrow(
            () -> equipo.maximoPrecioPorCategoria()
        );

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @Order(10)
    void verificarSegundaCategoriaMaximoPrecioPorCategoria() {
        var resultado = equipo.maximoPrecioPorCategoria();

        assertEquals("Esquí", resultado.get(1).getCategoria());
    }

    // =========================================================
    // 3. EQUIPOS ALQUILADOS AL MENOS DOS VECES
    // =========================================================

    @Test
    @Order(11)
    void verificarResultadoEquiposAlquiladosDosVeces() {
        var resultado = alquiler.equiposAlquiladosDosVeces();

        assertNotNull(resultado);
        assertEquals(5, resultado.size());
    }

    @Test
    @Order(12)
    void verificarOrdenacionEquiposAlquiladosDosVeces() {
        var resultado = alquiler.equiposAlquiladosDosVeces();

        assertFalse(resultado.isEmpty());
        assertEquals("BICI-002", resultado.get(0).getCodigo());
    }

    @Test
    @Order(13)
    void verificarImplementacionEquiposAlquiladosDosVeces() {
        alquiler.equiposAlquiladosDosVeces();

        estructura("having", "group by", "count(");
    }

    @Test
    @Order(14)
    void verificarResultadoVacioEquiposAlquiladosDosVeces() {
        sql("DELETE FROM alquileres");

        var resultado = assertDoesNotThrow(
            () -> alquiler.equiposAlquiladosDosVeces()
        );

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @Order(15)
    void verificarCantidadPrimerEquipoEquiposAlquiladosDosVeces() {
        var resultado = alquiler.equiposAlquiladosDosVeces();

        assertEquals(
            3L,
            resultado.get(0).getTotal().longValue()
        );
    }

    // =========================================================
    // 4. TRES EQUIPOS CON MAYOR RECAUDACIÓN
    // =========================================================

    @Test
    @Order(16)
    void verificarResultadoTresEquiposMasIngresos() {
        var resultado = alquiler.tresEquiposMasIngresos();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
    }

    @Test
    @Order(17)
    void verificarOrdenacionTresEquiposMasIngresos() {
        var resultado = alquiler.tresEquiposMasIngresos();

        assertFalse(resultado.isEmpty());
        assertEquals("BICI-002", resultado.get(0).getCodigo());
    }

    @Test
    @Order(18)
    void verificarImplementacionTresEquiposMasIngresos() {
        alquiler.tresEquiposMasIngresos();

        estructura("sum(", "group by", "limit");
    }

    @Test
    @Order(19)
    void verificarResultadoVacioTresEquiposMasIngresos() {
        sql("DELETE FROM alquileres");

        var resultado = assertDoesNotThrow(
            () -> alquiler.tresEquiposMasIngresos()
        );

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @Order(20)
    void verificarIngresosPrimerEquipoTresEquiposMasIngresos() {
        var resultado = alquiler.tresEquiposMasIngresos();

        BigDecimal ingresosEsperados = new BigDecimal("280");
        BigDecimal ingresosObtenidos = resultado.get(0).getIngresos();

        assertEquals(
            0,
            ingresosEsperados.compareTo(ingresosObtenidos)
        );
    }
}
