package Interface;

import model.Promocion;
import java.util.List;

public interface IPromocion {
    public List<Promocion> listar();
    public Promocion buscarPorId(int id);
    public Promocion buscarPorCodigo(String codigo);
    public int insertar(Promocion p);
    public int actualizar(Promocion p);
    public int eliminar(int id);
    public boolean toggleActivo(int id);
}
