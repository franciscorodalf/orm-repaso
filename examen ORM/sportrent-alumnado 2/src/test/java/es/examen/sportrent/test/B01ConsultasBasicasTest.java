
package es.examen.sportrent.test;

import es.examen.sportrent.dao.AlquilerDao;
import es.examen.sportrent.dao.ClienteDao;
import es.examen.sportrent.dao.EquipoDao;
import es.examen.sportrent.model.Alquiler;
import es.examen.sportrent.model.ClienteAlquileres;
import es.examen.sportrent.model.Equipo;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class B01ConsultasBasicasTest extends BaseTest {

    private final ClienteDao cliente = new ClienteDao();
    private final EquipoDao equipo = new EquipoDao();
    private final AlquilerDao alquiler = new AlquilerDao();

    // =========================================================
    // 1. ALQUILERES POR CLIENTE
    // =========================================================

    @Test
    @Order(1)
    void verificarResultadoAlquileresPorCliente() {
        List<ClienteAlquileres> resultado =
                cliente.alquileresPorCliente();

        assertNotNull(resultado);
        assertEquals(6, resultado.size());
    }

    @Test
    @Order(2)
    void verificarOrdenacionAlquileresPorCliente() {
        List<ClienteAlquileres> resultado =
                cliente.alquileresPorCliente();

        assertFalse(resultado.isEmpty());
        assertEquals("12345678A", resultado.get(0).getDni());
    }

    @Test
    @Order(3)
    void verificarImplementacionAlquileresPorCliente() {
        cliente.alquileresPorCliente();

        estructura("group by", "count(", "join");
    }

    @Test
    @Order(4)
    void verificarResultadoVacioAlquileresPorCliente() {
        sql("DELETE FROM alquileres");

        List<ClienteAlquileres> resultado = assertDoesNotThrow(
                () -> cliente.alquileresPorCliente()
        );

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================
    // 2. CINCO EQUIPOS MÁS CAROS
    // =========================================================

    @Test
    @Order(5)
    void verificarResultadoCincoEquiposMasCaros() {
        List<Equipo> resultado = equipo.cincoMasCaros();

        assertNotNull(resultado);
        assertEquals(5, resultado.size());
    }

    @Test
    @Order(6)
    void verificarOrdenacionCincoEquiposMasCaros() {
        List<Equipo> resultado = equipo.cincoMasCaros();

        assertFalse(resultado.isEmpty());
        assertEquals("SKI-002", resultado.get(0).getCodigo());
    }

    @Test
    @Order(7)
    void verificarImplementacionCincoEquiposMasCaros() {
        equipo.cincoMasCaros();

        estructura("order by", "limit");
    }

    @Test
    @Order(8)
    void verificarResultadoVacioCincoEquiposMasCaros() {
        sql("DELETE FROM alquileres");
        sql("DELETE FROM equipos");

        List<Equipo> resultado = assertDoesNotThrow(
                () -> equipo.cincoMasCaros()
        );

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================
    // 3. ALQUILERES PENDIENTES DE DEVOLUCIÓN
    // =========================================================

    @Test
    @Order(9)
    void verificarResultadoAlquileresPendientes() {
        List<Alquiler> resultado =
                alquiler.pendientesDevolucion();

        assertNotNull(resultado);
        assertEquals(5, resultado.size());
    }

    @Test
    @Order(10)
    void verificarOrdenacionAlquileresPendientes() {
        List<Alquiler> resultado =
                alquiler.pendientesDevolucion();

        assertFalse(resultado.isEmpty());

        assertEquals(
                10L,
                resultado.get(0).getId().longValue()
        );
    }

    @Test
    @Order(11)
    void verificarImplementacionAlquileresPendientes()
            throws IOException {

        // Ejecutamos el método que se está evaluando.
        alquiler.pendientesDevolucion();

        // Hibernate puede optimizar determinadas relaciones
        // al generar el SQL. Verificamos las condiciones
        // y la ordenación de la consulta ejecutada.

        estructura("where", "order by");

        // Comprobamos también los requisitos estructurales
        // en el código fuente del DAO.

        Path rutaDao = Path.of(
                "src/main/java/es/examen/sportrent/dao/"
                        + "AlquilerDao.java"
        );

        String codigoFuente = Files.readString(rutaDao);

        Pattern relacionCliente = Pattern.compile(
                "(?i)\\bjoin\\s+\\w+\\.cliente\\b"
        );

        Pattern relacionEquipo = Pattern.compile(
                "(?i)\\bjoin\\s+\\w+\\.equipo\\b"
        );

        assertTrue(
                relacionCliente.matcher(codigoFuente).find(),
                "La implementación no cumple los requisitos "
                        + "de la consulta de alquileres pendientes."
        );

        assertTrue(
                relacionEquipo.matcher(codigoFuente).find(),
                "La implementación no cumple los requisitos "
                        + "de la consulta de alquileres pendientes."
        );
    }

    @Test
    @Order(12)
    void verificarResultadoVacioAlquileresPendientes() {
        sql(
                "UPDATE alquileres "
                        + "SET fecha_devolucion = fecha_fin"
        );

        List<Alquiler> resultado = assertDoesNotThrow(
                () -> alquiler.pendientesDevolucion()
        );

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
