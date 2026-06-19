package Interface;

import java.util.List;
import model.Compra;

public interface ICompra {
    public List<Compra> lista();
    public int insertar(Compra compra);
    public int insertarLote(List<Compra> compras);
    public Compra searchById(int id);
    public boolean delete(int id);
    public List<Compra> listarPorCliente(int idCliente);
}
