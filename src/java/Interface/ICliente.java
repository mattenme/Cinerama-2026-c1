package Interface;

import java.util.List;
import model.Cliente;

public interface ICliente {
    public List<Cliente> lista();
    public int insertar(Cliente cli);
    public boolean update(Cliente cli);
    public Cliente searchById(int id);
    public boolean delete(int id);
    public Cliente searchByDni(String dni);
}
