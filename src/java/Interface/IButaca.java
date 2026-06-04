package Interface;

import java.util.List;
import model.Butaca;

public interface IButaca {
    public List<Butaca> lista();
    public List<Butaca> listarPorSala(int idSala);
    public boolean insertar(Butaca but);
    public boolean update(Butaca but);
    public Butaca searchById(int id);
    public boolean delete(int id);
    public boolean actualizarEstado(int id, String estado);
}
