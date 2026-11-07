package es.ejemplo.biblioteca.app;
import es.ejemplo.biblioteca.dao.*;

public final class Main {
    private Main() {}
    
    public static void main(String[] args) {
        String url="jdbc:sqlite:datos/biblioteca.sqlite3?foreign_keys=on";
        try(var socios=new SocioDao(url);var libros=new LibroDao(url)) {
            System.out.println("Socios: "+socios.listar().size());
            System.out.println("Libros: "+libros.listar().size());
        }
    }
}
