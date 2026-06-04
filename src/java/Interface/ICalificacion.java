package Interface;

import java.util.List;
import model.Calificacion;

public interface ICalificacion {
    public List<Calificacion> lista();
    public boolean insertar(Calificacion cal);
    public Calificacion searchById(int idCliente, int idPelicula);
    public List<Calificacion> listarPorPelicula(int idPelicula);
    public List<Calificacion> listarPorCliente(int idCliente);
    public boolean delete(int idCliente, int idPelicula);
}
