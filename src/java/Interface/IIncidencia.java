package Interface;

import java.util.List;
import model.Incidencia;

public interface IIncidencia {
    public List<Incidencia> lista();
    public boolean insertar(Incidencia inc);
    public boolean update(Incidencia inc);
    public Incidencia searchById(int id);
    public boolean delete(int id);
}
