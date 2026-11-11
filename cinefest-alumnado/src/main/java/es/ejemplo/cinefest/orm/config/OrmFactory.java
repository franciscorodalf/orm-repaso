package es.ejemplo.cinefest.orm.config;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Map;
public final class OrmFactory {
    private OrmFactory() {}
    public static EntityManagerFactory crear(String jdbcUrl) {
        return Persistence.createEntityManagerFactory("cinefestPU",
            Map.of("jakarta.persistence.jdbc.url", jdbcUrl));
    }
}
