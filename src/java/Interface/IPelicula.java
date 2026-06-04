package Interface;

import java.util.List;
import model.Pelicula;

public interface IPelicula {
    public List<Pelicula> lista();
    public boolean insertar(Pelicula pel);
    public boolean update(Pelicula pel);
    public Pelicula searchById(int id);
    public boolean delete(int id);
}
