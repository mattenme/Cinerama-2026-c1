package Interface;

import java.util.List;
import model.Pelicula;

public interface IPelicula {
    public List<Pelicula> lista();
    public List<Pelicula> listaPaginada(int start, int limit);
    public int contar();
    public boolean insertar(Pelicula pel);
    public boolean update(Pelicula pel);
    public Pelicula searchById(int id);
    public boolean delete(int id);
    public boolean toggleActivo(int id);
    public boolean toggleDestacado(int id);
}
