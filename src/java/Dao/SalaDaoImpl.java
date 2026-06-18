package Dao;

import Interface.ISala;
import model.Sala;
import utils.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaDaoImpl implements ISala {

    @Override
    public List<Sala> lista() {
        List<Sala> lista = new ArrayList<>();
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement("SELECT * FROM Sala");
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                lista.add(mapping(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public int insertar(Sala sala) {
        String sql = "INSERT INTO Sala (nombre, tipo, capacidad_total) VALUES (?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql, new String[]{"id_sala"})) {
            st.setString(1, sala.getNombre());
            st.setString(2, sala.getTipo());
            st.setInt(3, sala.getCapacidad_total());
            int affected = st.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean update(Sala sala) {
        String sql = "UPDATE Sala SET nombre=?, tipo=?, capacidad_total=? WHERE id_sala=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, sala.getNombre());
            st.setString(2, sala.getTipo());
            st.setInt(3, sala.getCapacidad_total());
            st.setInt(4, sala.getId_sala());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Sala searchById(int id) {
        Sala sala = null;
        String sql = "SELECT * FROM Sala WHERE id_sala=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) sala = mapping(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sala;
    }

    @Override
    public boolean existeNombre(String nombre) {
        String sql = "SELECT COUNT(*) FROM Sala WHERE nombre=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, nombre);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Sala WHERE id_sala=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Sala mapping(ResultSet rs) throws SQLException {
        return new Sala(
            rs.getInt("id_sala"),
            rs.getString("nombre"),
            rs.getString("tipo"),
            rs.getInt("capacidad_total")
        );
    }
}
