
package es.examen.sportrent.test;

import es.examen.sportrent.dao.ClienteDao;
import es.examen.sportrent.dao.EquipoDao;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class B03ConsultasAvanzadasTest extends BaseTest {

    private final ClienteDao cliente = new ClienteDao();
    private final EquipoDao equipo = new EquipoDao();

    // =========================================================
    // 1. CLIENTES QUE SUPERAN UNA REFERENCIA
    // =========================================================

    @Test
    @Order(1)
    void verificarResultadoClientesSuperanReferencia() {
        var resultado = cliente.clientesSuperanReferencia("34567890C");

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    @Order(2)
    void verificarOrdenacionClientesSuperanReferencia() {
        var resultado = cliente.clientesSuperanReferencia("34567890C");

        assertFalse(resultado.isEmpty());
        assertEquals("12345678A", resultado.get(0).getDni());
    }

    @Test
    @Order(3)
    void verificarImplementacionClientesSuperanReferencia() {
        cliente.clientesSuperanReferencia("34567890C");

        estructura("having", "select", "count(");
    }

    @Test
    @Order(4)
    void verificarResultadoVacioClientesSuperanReferencia() {
        sql("DELETE FROM alquileres");

        var resultado = assertDoesNotThrow(
            () -> cliente.clientesSuperanReferencia("34567890C")
        );

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @Order(5)
    void verificarSegundoClienteClientesSuperanReferencia() {
        var resultado = cliente.clientesSuperanReferencia("34567890C");

        assertEquals("23456789B", resultado.get(1).getDni());
    }

    @Test
    @Order(6)
    void verificarCantidadPrimerClienteClientesSuperanReferencia() {
        var resultado = cliente.clientesSuperanReferencia("34567890C");

        assertEquals(
            4L,
            resultado.get(0).getTotal().longValue()
        );
    }

    @Test
    @Order(7)
    void verificarCantidadSegundoClienteClientesSuperanReferencia() {
        var resultado = cliente.clientesSuperanReferencia("34567890C");

        assertEquals(
            3L,
            resultado.get(1).getTotal().longValue()
        );
    }

    @Test
    @Order(8)
    void verificarReferenciaInexistenteClientesSuperanReferencia() {
        var resultado = cliente.clientesSuperanReferencia("99999999Z");

        assertTrue(resultado.isEmpty());
    }

    @Test
    @Order(9)
    void verificarReferenciaNulaClientesSuperanReferencia() {
        var resultado = cliente.clientesSuperanReferencia(null);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @Order(10)
    void verificarReferenciaConUnAlquilerClientesSuperanReferencia() {
        var resultado = cliente.clientesSuperanReferencia("45678901D");

        assertEquals(5, resultado.size());
    }

    @Test
    @Order(11)
    void verificarReferenciaSinAlquileresClientesSuperanReferencia() {
        var resultado = cliente.clientesSuperanReferencia("78901234G");

        assertEquals(6, resultado.size());
    }

    @Test
    @Order(12)
    void verificarReferenciaConMayorCantidadClientesSuperanReferencia() {
        var resultado = cliente.clientesSuperanReferencia("12345678A");

        assertTrue(resultado.isEmpty());
    }

    // =========================================================
    // 2. EQUIPOS CON PRECIO SUPERIOR A LA MEDIA DE SU CATEGORÍA
    // =========================================================

    @Test
    @Order(13)
    void verificarResultadoEquiposSobreMediaCategoria() {
        var resultado = equipo.equiposSobreMediaCategoria();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
    }

    @Test
    @Order(14)
    void verificarOrdenacionEquiposSobreMediaCategoria() {
        var resultado = equipo.equiposSobreMediaCategoria();

        assertFalse(resultado.isEmpty());
        assertEquals("BICI-002", resultado.get(0).getCodigo());
    }

    @Test
    @Order(15)
    void verificarImplementacionEquiposSobreMediaCategoria() {
        equipo.equiposSobreMediaCategoria();

        estructura("select", "avg(", "where");
    }

    @Test
    @Order(16)
    void verificarResultadoVacioEquiposSobreMediaCategoria() {
        sql("DELETE FROM alquileres");
        sql("DELETE FROM equipos");

        var resultado = assertDoesNotThrow(
            () -> equipo.equiposSobreMediaCategoria()
        );

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @Order(17)
    void verificarSegundoEquipoEquiposSobreMediaCategoria() {
        var resultado = equipo.equiposSobreMediaCategoria();

        assertEquals("SKI-002", resultado.get(1).getCodigo());
    }

    @Test
    @Order(18)
    void verificarTercerEquipoEquiposSobreMediaCategoria() {
        var resultado = equipo.equiposSobreMediaCategoria();

        assertEquals("SURF-002", resultado.get(2).getCodigo());
    }

    @Test
    @Order(19)
    void verificarComparacionDePreciosEquiposSobreMediaCategoria() {
        var resultado = equipo.equiposSobreMediaCategoria();

        assertTrue(
            resultado.get(0).getPrecioDia().doubleValue()
                > resultado.get(0).getMediaCategoria()
        );
    }

    @Test
    @Order(20)
    void verificarCategoriaPrimerEquipoEquiposSobreMediaCategoria() {
        var resultado = equipo.equiposSobreMediaCategoria();

        assertEquals("Ciclismo", resultado.get(0).getCategoria());
    }

    @Test
    @Order(21)
    void verificarMediasInformadasEquiposSobreMediaCategoria() {
        var resultado = equipo.equiposSobreMediaCategoria();

        assertTrue(
            resultado.stream()
                .allMatch(equipo -> equipo.getMediaCategoria() != null)
        );
    }

    @Test
    @Order(22)
    void verificarPrecioPrimerEquipoEquiposSobreMediaCategoria() {
        var resultado = equipo.equiposSobreMediaCategoria();

        BigDecimal precioEsperado = new BigDecimal("35");
        BigDecimal precioObtenido = resultado.get(0).getPrecioDia();

        assertEquals(
            0,
            precioEsperado.compareTo(precioObtenido)
        );
    }

    @Test
    @Order(23)
    void verificarResultadoEstableEquiposSobreMediaCategoria() {
        var resultado = assertDoesNotThrow(
            () -> equipo.equiposSobreMediaCategoria()
        );

        assertTrue(resultado.size() == 3);
    }
}
