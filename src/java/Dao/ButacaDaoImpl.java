package Dao;

import Interface.IButaca;
import model.Butaca;
import model.Sala;
import utils.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ButacaDaoImpl implements IButaca {

    @Override
    public List<Butaca> lista() {
        List<Butaca> lista = new ArrayList<>();
        String sql = "SELECT b.*, s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total as sala_capacidad "
                   + "FROM Butaca b JOIN Sala s ON b.id_sala = s.id_sala ORDER BY b.id_sala, b.fila, b.numero";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapping(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Butaca> listarPorSala(int idSala) {
        List<Butaca> lista = new ArrayList<>();
        String sql = "SELECT b.*, s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total as sala_capacidad "
                   + "FROM Butaca b JOIN Sala s ON b.id_sala = s.id_sala WHERE b.id_sala=? ORDER BY b.fila, b.numero";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, idSala);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) lista.add(mapping(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertar(Butaca but) {
        String sql = "INSERT INTO Butaca (id_sala, fila, numero, estado) VALUES (?, ?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, but.getSala().getId_sala());
            st.setString(2, but.getFila());
            st.setInt(3, but.getNumero());
            st.setString(4, but.getEstado());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Butaca but) {
        String sql = "UPDATE Butaca SET id_sala=?, fila=?, numero=?, estado=? WHERE id_butaca=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, but.getSala().getId_sala());
            st.setString(2, but.getFila());
            st.setInt(3, but.getNumero());
            st.setString(4, but.getEstado());
            st.setInt(5, but.getId_butaca());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Butaca searchById(int id) {
        Butaca but = null;
        String sql = "SELECT b.*, s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total as sala_capacidad "
                   + "FROM Butaca b JOIN Sala s ON b.id_sala = s.id_sala WHERE b.id_butaca=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) but = mapping(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return but;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Butaca WHERE id_butaca=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizarEstado(int id, String estado) {
        String sql = "UPDATE Butaca SET estado=? WHERE id_butaca=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, estado);
            st.setInt(2, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Butaca mapping(ResultSet rs) throws SQLException {
        Sala sala = new Sala(
            rs.getInt("id_sala"),
            rs.getString("sala_nombre"),
            rs.getString("sala_tipo"),
            rs.getInt("sala_capacidad")
        );
        return new Butaca(
            rs.getInt("id_butaca"),
            sala,
            rs.getString("fila"),
            rs.getInt("numero"),
            rs.getString("estado")
        );
    }
}
