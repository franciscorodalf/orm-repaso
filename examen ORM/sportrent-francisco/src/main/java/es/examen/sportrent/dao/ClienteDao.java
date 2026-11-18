package es.examen.sportrent.dao;
import es.examen.sportrent.model.Cliente;
import es.examen.sportrent.model.ClienteAlquileres;

import java.util.List;

public class ClienteDao {

    public List<Cliente> listarTodos() {
        throw new UnsupportedOperationException("No implementado");
    }

    public Cliente buscarPorId(String dni) {
        throw new UnsupportedOperationException("No implementado");
    }

    public boolean insertar(Cliente m) {
        throw new UnsupportedOperationException("No implementado");    }

    public boolean actualizar(Cliente m) {
        throw new UnsupportedOperationException("No implementado");
    }

    public boolean eliminar(String id) {
        throw new UnsupportedOperationException("No implementado");
    }

    public List<ClienteAlquileres> alquileresPorCliente() {
        throw new UnsupportedOperationException("No implementado");    
    }
    
    public List<ClienteAlquileres> clientesConMasDeDosAlquileres() {
        throw new UnsupportedOperationException("No implementado");
    }

    public List<ClienteAlquileres> clientesSuperanReferencia(String dniReferencia) {
        throw new UnsupportedOperationException("No implementado");    }
}
