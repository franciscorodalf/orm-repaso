package es.examen.sportrent.test.config;
import jakarta.persistence.*;
import java.util.*;
/** Proporciona conexiones JPA; configurable por las pruebas aisladas. */
public final class JpaUtil {
    private static EntityManagerFactory factory;
    private static String database = "datos/sportrent.db";
    private JpaUtil() { }
    public static synchronized void configurar(String path) {
        cerrar(); database=path;
    }
    public static synchronized EntityManager abrir() {
        if (factory == null) {
            Map<String,Object> props = new HashMap<>();
            props.put("jakarta.persistence.jdbc.url", "jdbc:sqlite:"+database);
            props.put("hibernate.session_factory.statement_inspector", SqlCapture.class.getName());
            factory = Persistence.createEntityManagerFactory("sportrent", props);
        }
        return factory.createEntityManager();
    }
    public static synchronized void cerrar() {
        if (factory != null) { factory.close(); factory=null; }
    }
}
