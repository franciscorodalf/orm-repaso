package es.ejemplo.biblioteca.infra;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionBD {
    private ConexionBD() {}
    
    public static Connection abrir(String url) throws SQLException {
        Connection c=DriverManager.getConnection(url);
        try (var st=c.createStatement()) { st.execute("PRAGMA foreign_keys=ON"); }
        return c;
    }
}
