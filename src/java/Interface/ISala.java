package Interface;

import java.util.List;
import model.Sala;

public interface ISala {
    public List<Sala> lista();
    public int insertar(Sala sala);
    public int insertarConAsientos(Sala sala, int columnas);
    public boolean update(Sala sala);
    public Sala searchById(int id);
    public boolean delete(int id);
    public boolean existeNombre(String nombre);
    public boolean toggleActivo(int id);
}
