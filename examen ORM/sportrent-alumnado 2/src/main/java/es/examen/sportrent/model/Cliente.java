package es.examen.sportrent.model;

public class Cliente {
    private String dni;
    private String nombre;
    private String email;
    private String telefono;
    public Cliente() { }
    public Cliente(String dni, String nombre, String email, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }
    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public void setDni(String dni) {
        this.dni = dni;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}