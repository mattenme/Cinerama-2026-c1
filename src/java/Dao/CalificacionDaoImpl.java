package Dao;

import Interface.ICalificacion;
import model.Calificacion;
import utils.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CalificacionDaoImpl implements ICalificacion {

    @Override
    public List<Calificacion> lista() {
        List<Calificacion> lista = new ArrayList<>();
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement("SELECT * FROM Calificacion");
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
    public boolean insertar(Calificacion cal) {
        String sql = "INSERT INTO Calificacion (id_cliente, id_pelicula, puntuacion) VALUES (?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, cal.getId_cliente());
            st.setInt(2, cal.getId_pelicula());
            st.setInt(3, cal.getPuntuacion());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Calificacion searchById(int idCliente, int idPelicula) {
        Calificacion cal = null;
        String sql = "SELECT * FROM Calificacion WHERE id_cliente=? AND id_pelicula=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, idCliente);
            st.setInt(2, idPelicula);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) cal = mapping(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cal;
    }

    @Override
    public List<Calificacion> listarPorPelicula(int idPelicula) {
        List<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM Calificacion WHERE id_pelicula=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, idPelicula);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) lista.add(mapping(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Calificacion> listarPorCliente(int idCliente) {
        List<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM Calificacion WHERE id_cliente=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, idCliente);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) lista.add(mapping(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean delete(int idCliente, int idPelicula) {
        String sql = "DELETE FROM Calificacion WHERE id_cliente=? AND id_pelicula=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, idCliente);
            st.setInt(2, idPelicula);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Calificacion mapping(ResultSet rs) throws SQLException {
        Calificacion cal = new Calificacion();
        cal.setId_cliente(rs.getInt("id_cliente"));
        cal.setId_pelicula(rs.getInt("id_pelicula"));
        cal.setPuntuacion(rs.getInt("puntuacion"));
        Timestamp ts = rs.getTimestamp("fecha_calificacion");
        cal.setFecha_calificacion(ts);
        return cal;
    }
}
