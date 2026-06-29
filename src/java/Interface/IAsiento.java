package Interface;

import java.util.List;
import model.Asiento;

public interface IAsiento {
    public List<Asiento> lista();
    public List<Asiento> listarPorSala(int idSala);
    public boolean insertar(Asiento a);
    public boolean update(Asiento a);
    public Asiento searchById(int id);
    public boolean delete(int id);
    public boolean actualizarEstado(int id, String estado);
    public int liberarReservadas(int idSala);
}
