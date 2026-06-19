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
    public int insertarConButacas(Sala sala, int columnas) {
        Connection cn = null;
        PreparedStatement stSala = null;
        PreparedStatement stButaca = null;
        ResultSet rs = null;
        try {
            cn = ConexionSingleton.getConnection();
            cn.setAutoCommit(false);
            stSala = cn.prepareStatement(
                "INSERT INTO Sala (nombre, tipo, capacidad_total) VALUES (?, ?, ?)",
                new String[]{"id_sala"}
            );
            stSala.setString(1, sala.getNombre());
            stSala.setString(2, sala.getTipo());
            stSala.setInt(3, sala.getCapacidad_total());
            int affected = stSala.executeUpdate();
            if (affected == 0) throw new SQLException("No se pudo insertar la sala");
            rs = stSala.getGeneratedKeys();
            if (!rs.next()) throw new SQLException("No se gener\u00F3 ID de sala");
            int salaId = rs.getInt(1);
            rs.close();
            rs = null;
            int cap = sala.getCapacidad_total();
            int rows = (int) Math.ceil((double) cap / columnas);
            stButaca = cn.prepareStatement("INSERT INTO Butaca (id_sala, fila, numero, estado) VALUES (?, ?, ?, ?)");
            int creados = 0;
            for (int r = 0; r < rows && creados < cap; r++) {
                char filaChar = (char) ('A' + r);
                for (int n = 1; n <= columnas && creados < cap; n++) {
                    stButaca.setInt(1, salaId);
                    stButaca.setString(2, String.valueOf(filaChar));
                    stButaca.setInt(3, n);
                    stButaca.setString(4, "Disponible");
                    stButaca.addBatch();
                    creados++;
                }
            }
            stButaca.executeBatch();
            cn.commit();
            return salaId;
        } catch (SQLException e) {
            e.printStackTrace();
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { }
            try { if (stSala != null) stSala.close(); } catch (SQLException e) { }
            try { if (stButaca != null) stButaca.close(); } catch (SQLException e) { }
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return -1;
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
