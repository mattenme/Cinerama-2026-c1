package Dao;

import Interface.IFuncion;
import model.*;
import utils.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionDaoImpl implements IFuncion {

    @Override
    public List<Funcion> lista() {
        List<Funcion> lista = new ArrayList<>();
        String sql = "SELECT f.*, p.titulo, p.duracion_minutos, "
                   + "s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total as sala_capacidad "
                   + "FROM Funcion f "
                   + "JOIN Pelicula p ON f.id_pelicula = p.id_pelicula "
                   + "JOIN Sala s ON f.id_sala = s.id_sala";
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
    public boolean insertar(Funcion fun) {
        String sql = "INSERT INTO Funcion (id_pelicula, id_sala, hora_inicio, estado) VALUES (?, ?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, fun.getPelicula().getId_pelicula());
            st.setInt(2, fun.getSala().getId_sala());
            st.setString(3, fun.getHora_inicio());
            st.setString(4, fun.getEstado());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Funcion fun) {
        String sql = "UPDATE Funcion SET id_pelicula=?, id_sala=?, hora_inicio=?, estado=? WHERE id_funcion=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, fun.getPelicula().getId_pelicula());
            st.setInt(2, fun.getSala().getId_sala());
            st.setString(3, fun.getHora_inicio());
            st.setString(4, fun.getEstado());
            st.setInt(5, fun.getId_funcion());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Funcion searchById(int id) {
        Funcion fun = null;
        String sql = "SELECT f.*, p.titulo, p.duracion_minutos, "
                   + "s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total as sala_capacidad "
                   + "FROM Funcion f "
                   + "JOIN Pelicula p ON f.id_pelicula = p.id_pelicula "
                   + "JOIN Sala s ON f.id_sala = s.id_sala WHERE f.id_funcion=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) fun = mapping(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fun;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Funcion WHERE id_funcion=?";
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
    public List<Funcion> listarPorPelicula(int idPelicula) {
        List<Funcion> lista = new ArrayList<>();
        String sql = "SELECT f.*, p.titulo, p.duracion_minutos, "
                   + "s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total as sala_capacidad "
                   + "FROM Funcion f "
                   + "JOIN Pelicula p ON f.id_pelicula = p.id_pelicula "
                   + "JOIN Sala s ON f.id_sala = s.id_sala WHERE f.id_pelicula=?";
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

    private Funcion mapping(ResultSet rs) throws SQLException {
        Pelicula pel = new Pelicula(
            rs.getInt("id_pelicula"),
            rs.getString("titulo"),
            rs.getInt("duracion_minutos")
        );
        Sala sala = new Sala(
            rs.getInt("id_sala"),
            rs.getString("sala_nombre"),
            rs.getString("sala_tipo"),
            rs.getInt("sala_capacidad")
        );
        return new Funcion(
            rs.getInt("id_funcion"),
            pel,
            sala,
            rs.getString("hora_inicio"),
            rs.getString("estado")
        );
    }
}
