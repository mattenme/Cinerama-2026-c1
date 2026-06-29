package Interface;

import java.util.List;
import model.Producto;

public interface IProducto {
    public List<Producto> lista();
    public int insertar(Producto prod);
    public boolean update(Producto prod);
    public Producto searchById(int id);
    public boolean delete(int id);
    public List<Producto> listarActivos();
    public boolean toggleActivo(int id);
}
