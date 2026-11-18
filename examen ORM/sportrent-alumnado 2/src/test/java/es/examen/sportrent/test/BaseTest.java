package es.examen.sportrent.test;
import org.junit.jupiter.api.*;

import es.examen.sportrent.test.config.*;

import java.nio.file.*;
import java.sql.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
abstract class BaseTest {
    private Path temporal;
    @BeforeEach void prepararBd() throws IOException {
        temporal=Files.createTempFile("sportrent-test-", ".db");
        Files.copy(Path.of("datos/sportrent.db"),temporal,StandardCopyOption.REPLACE_EXISTING);
        JpaUtil.configurar(temporal.toAbsolutePath().toString());
        SqlCapture.limpiar();
    }
    @AfterEach void limpiarBd() throws IOException {
        JpaUtil.cerrar();SqlCapture.finalizar();Files.deleteIfExists(temporal);
    }
    void sql(String query) {
        try(Connection c=DriverManager.getConnection("jdbc:sqlite:"+temporal.toAbsolutePath());Statement st=c.createStatement()) {st.executeUpdate(query);}
        catch(SQLException ex){ throw new AssertionError(ex); }
    }
    String sqlCapturado() { return String.join(" ",SqlCapture.obtener()); }
    void estructura(String... tokens) {
        String sentencias=sqlCapturado();
        assertFalse(sentencias.isEmpty(),"No se ejecutó ninguna sentencia SQL");
        for(String token:tokens) assertTrue(sentencias.contains(token.toLowerCase()),"Falta SQL generado: "+token+". SQL="+sentencias);
    }
}
