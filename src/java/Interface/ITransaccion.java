package Interface;

import java.util.List;
import model.Transaccion;

public interface ITransaccion {
    public List<Transaccion> lista();
    public int insertar(Transaccion trans);
    public Transaccion searchById(int id);
    public boolean delete(int id);
    public List<Transaccion> listarPorCliente(int idCliente);
}
