package Dao;

import Interface.IAsiento;
import model.Asiento;
import model.Sala;
import utils.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsientoDaoImpl implements IAsiento {

    @Override
    public List<Asiento> lista() {
        List<Asiento> lista = new ArrayList<>();
        String sql = "SELECT a.*, s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total as sala_capacidad, s.activo as sala_activo "
                   + "FROM Asiento a JOIN Sala s ON a.id_sala = s.id_sala ORDER BY a.id_sala, a.fila, a.numero";
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
    public List<Asiento> listarPorSala(int idSala) {
        List<Asiento> lista = new ArrayList<>();
        String sql = "SELECT a.*, s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total as sala_capacidad, s.activo as sala_activo "
                   + "FROM Asiento a JOIN Sala s ON a.id_sala = s.id_sala WHERE a.id_sala=? ORDER BY a.fila, a.numero";
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
    public boolean insertar(Asiento a) {
        String sql = "INSERT INTO Asiento (id_sala, fila, numero, estado) VALUES (?, ?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, a.getSala().getId_sala());
            st.setString(2, a.getFila());
            st.setInt(3, a.getNumero());
            st.setString(4, a.getEstado());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Asiento a) {
        String sql = "UPDATE Asiento SET id_sala=?, fila=?, numero=?, estado=? WHERE id_asiento=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, a.getSala().getId_sala());
            st.setString(2, a.getFila());
            st.setInt(3, a.getNumero());
            st.setString(4, a.getEstado());
            st.setInt(5, a.getId_asiento());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Asiento searchById(int id) {
        Asiento a = null;
        String sql = "SELECT a.*, s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total as sala_capacidad, s.activo as sala_activo "
                   + "FROM Asiento a JOIN Sala s ON a.id_sala = s.id_sala WHERE a.id_asiento=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) a = mapping(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return a;
    }

    @Override
    public boolean delete(int id) {
        Connection cn = null;
        PreparedStatement stCompra = null;
        PreparedStatement stAsiento = null;
        try {
            cn = ConexionSingleton.getConnection();
            cn.setAutoCommit(false);
            stCompra = cn.prepareStatement("DELETE FROM Compra WHERE id_asiento=?");
            stCompra.setInt(1, id);
            stCompra.executeUpdate();
            stAsiento = cn.prepareStatement("DELETE FROM Asiento WHERE id_asiento=?");
            stAsiento.setInt(1, id);
            int r = stAsiento.executeUpdate();
            cn.commit();
            return r > 0;
        } catch (SQLException e) {
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            try { if (stCompra != null) stCompra.close(); } catch (SQLException e) { }
            try { if (stAsiento != null) stAsiento.close(); } catch (SQLException e) { }
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    @Override
    public boolean actualizarEstado(int id, String estado) {
        String sql = "UPDATE Asiento SET estado=? WHERE id_asiento=?";
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

    @Override
    public int liberarReservadas(int idSala) {
        String sql = "UPDATE Asiento SET estado='Disponible' WHERE id_sala=? AND estado='Seleccionada'";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, idSala);
            return st.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error en liberarReservadas idSala=" + idSala + ": " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    private Asiento mapping(ResultSet rs) throws SQLException {
        Sala sala = new Sala(
            rs.getInt("id_sala"),
            rs.getString("sala_nombre"),
            rs.getString("sala_tipo"),
            rs.getInt("sala_capacidad"),
            rs.getInt("sala_activo")
        );
        return new Asiento(
            rs.getInt("id_asiento"),
            sala,
            rs.getString("fila"),
            rs.getInt("numero"),
            rs.getString("estado")
        );
    }
}
