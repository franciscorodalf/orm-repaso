package es.examen.sportrent.test.config;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import java.util.*;

public class SqlCapture implements StatementInspector {
    private static final ThreadLocal<List<String>> SQL=ThreadLocal.withInitial(ArrayList::new);
    @Override public String inspect(String sql) { SQL.get().add(sql.toLowerCase(Locale.ROOT)); return sql; }
    public static void limpiar() { SQL.get().clear(); }
    public static List<String> obtener() { return List.copyOf(SQL.get()); }
    public static void finalizar() { SQL.remove(); }
}
