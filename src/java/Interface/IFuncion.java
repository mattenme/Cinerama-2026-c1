package Interface;

import java.util.List;
import model.Funcion;

public interface IFuncion {
    public List<Funcion> lista();
    public boolean insertar(Funcion fun);
    public boolean update(Funcion fun);
    public Funcion searchById(int id);
    public boolean delete(int id);
    public List<Funcion> listarPorPelicula(int idPelicula);
}
