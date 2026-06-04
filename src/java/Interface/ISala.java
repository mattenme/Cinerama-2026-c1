package Interface;

import java.util.List;
import model.Sala;

public interface ISala {
    public List<Sala> lista();
    public int insertar(Sala sala);
    public boolean update(Sala sala);
    public Sala searchById(int id);
    public boolean delete(int id);
}
