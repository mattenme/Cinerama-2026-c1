package Dao;

import Interface.IPromocion;
import model.Promocion;
import utils.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromocionDaoImpl implements IPromocion {

    @Override
    public List<Promocion> listar() {
        List<Promocion> lista = new ArrayList<>();
        String sql = "SELECT * FROM Promocion ORDER BY id_promocion";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Promocion buscarPorId(int id) {
        String sql = "SELECT * FROM Promocion WHERE id_promocion=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Promocion buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM Promocion WHERE codigo=? AND activo=1";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, codigo);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int insertar(Promocion p) {
        String sql = "INSERT INTO Promocion (codigo, descripcion, descuento, activo) VALUES (?,?,?,?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, p.getCodigo());
            st.setString(2, p.getDescripcion());
            st.setInt(3, p.getDescuento());
            st.setInt(4, p.getActivo());
            return st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int actualizar(Promocion p) {
        String sql = "UPDATE Promocion SET codigo=?, descripcion=?, descuento=?, activo=? WHERE id_promocion=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, p.getCodigo());
            st.setString(2, p.getDescripcion());
            st.setInt(3, p.getDescuento());
            st.setInt(4, p.getActivo());
            st.setInt(5, p.getId_promocion());
            return st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int eliminar(int id) {
        String sql = "DELETE FROM Promocion WHERE id_promocion=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            return st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Promocion mapear(ResultSet rs) throws SQLException {
        return new Promocion(
            rs.getInt("id_promocion"),
            rs.getString("codigo"),
            rs.getString("descripcion"),
            rs.getInt("descuento"),
            rs.getInt("activo")
        );
    }
}
