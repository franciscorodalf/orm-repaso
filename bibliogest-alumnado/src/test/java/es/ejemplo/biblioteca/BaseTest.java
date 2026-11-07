package es.ejemplo.biblioteca;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.nio.file.*;
import java.sql.*;
import es.ejemplo.biblioteca.dao.*;

/** Crea una base SQLite separada por prueba y cierra los cinco DAO. */
public abstract class BaseTest {
    protected String url;
    protected SocioDao socios;
    protected AutorDao autores;
    protected LibroDao libros;
    protected PrestamoDao prestamos;
    protected ReservaDao reservas;
    private jakarta.persistence.EntityManagerFactory factory;
    private Path db;

    @BeforeEach
    void iniciar() throws Exception {
        db = Files.createTempFile("bibliogest-test-", ".sqlite3");
        url = "jdbc:sqlite:" + db + "?foreign_keys=on";
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:" + db)) {
            try (var st = con.createStatement()) {
                st.execute("PRAGMA foreign_keys=ON");
                String ddl = Files.readString(Path.of("src/main/resources/schema.sql"));
                for (String s : ddl.split(";"))
                    if (!s.isBlank())
                        st.execute(s);
            }
        }
        factory = es.ejemplo.biblioteca.orm.config.OrmFactory.crear(url);
        socios = new SocioDao(factory);
        autores = new AutorDao(factory);
        libros = new LibroDao(factory);
        prestamos = new PrestamoDao(factory);
        reservas = new ReservaDao(factory);
    }

    @AfterEach
    void finalizar() throws Exception {
        reservas.close();
        prestamos.close();
        libros.close();
        autores.close();
        socios.close();
        factory.close();
        Files.deleteIfExists(db);
    }

    protected int socio() {
        return socios.insertar(
                new es.ejemplo.biblioteca.modelo.Socio(null, "Ana", "ana@prueba.test", "123", "2025-01-01", true));
    }

    protected int autor() {
        return autores.insertar(new es.ejemplo.biblioteca.modelo.Autor(null, "Autora", "España", 1980));
    }

    protected int libro() {
        return libros.insertar(
                new es.ejemplo.biblioteca.modelo.Libro(null, "Libro prueba", "ISBN-001", "Novela", 2020, 2, autor()));
    }

    protected int prestamo() {
        return prestamos.registrarPrestamo(socio(), libro(), "2025-01-01", "2025-02-01");
    }

    protected int reserva() {
        return reservas
                .insertar(new es.ejemplo.biblioteca.modelo.Reserva(null, socio(), libro(), "2025-01-01", "PENDIENTE"));
    }
}
