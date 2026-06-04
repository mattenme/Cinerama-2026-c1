package Dao;

import Interface.IBoletoDetalle;
import model.*;
import utils.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoletoDetalleDaoImpl implements IBoletoDetalle {

    @Override
    public List<BoletoDetalle> listarPorTransaccion(int idTransaccion) {
        List<BoletoDetalle> lista = new ArrayList<>();
        String sql = "SELECT bd.*, b.id_sala, b.fila, b.numero, b.estado as but_estado, "
                   + "s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total "
                   + "FROM Boleto_Detalle bd "
                   + "JOIN Butaca b ON bd.id_butaca = b.id_butaca "
                   + "JOIN Sala s ON b.id_sala = s.id_sala "
                   + "WHERE bd.id_transaccion=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, idTransaccion);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) lista.add(mapping(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertar(BoletoDetalle boleto) {
        String sql = "INSERT INTO Boleto_Detalle (id_transaccion, id_butaca, precio_aplicado) VALUES (?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, boleto.getTransaccion().getId_transaccion());
            st.setInt(2, boleto.getButaca().getId_butaca());
            st.setDouble(3, boleto.getPrecio_aplicado());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean marcarQrUsado(int idBoleto) {
        String sql = "UPDATE Boleto_Detalle SET qr_usado=1 WHERE id_boleto=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, idBoleto);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private BoletoDetalle mapping(ResultSet rs) throws SQLException {
        Sala sala = new Sala(
            rs.getInt("id_sala"),
            rs.getString("sala_nombre"),
            rs.getString("sala_tipo"),
            rs.getInt("capacidad_total")
        );
        Butaca but = new Butaca(
            rs.getInt("id_butaca"),
            sala,
            rs.getString("fila"),
            rs.getInt("numero"),
            rs.getString("but_estado")
        );
        BoletoDetalle bd = new BoletoDetalle();
        bd.setId_boleto(rs.getInt("id_boleto"));
        bd.setButaca(but);
        bd.setPrecio_aplicado(rs.getDouble("precio_aplicado"));
        bd.setQr_usado(rs.getBoolean("qr_usado"));
        return bd;
    }
}
