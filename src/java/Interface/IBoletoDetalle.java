package Interface;

import java.util.List;
import model.BoletoDetalle;

public interface IBoletoDetalle {
    public List<BoletoDetalle> listarPorTransaccion(int idTransaccion);
    public boolean insertar(BoletoDetalle boleto);
    public boolean marcarQrUsado(int idBoleto);
}
