package Dao;

import Interface.IPelicula;
import model.Pelicula;
import utils.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDaoImpl implements IPelicula {

    @Override
    public List<Pelicula> lista() {
        List<Pelicula> lista = new ArrayList<>();
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement("SELECT * FROM Pelicula");
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
    public boolean insertar(Pelicula pel) {
        String sql = "INSERT INTO Pelicula (titulo, duracion_minutos, genero, sinopsis, imagen_url, destacado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, pel.getTitulo());
            st.setInt(2, pel.getDuracion_minutos());
            st.setString(3, pel.getGenero());
            st.setString(4, pel.getSinopsis());
            st.setString(5, pel.getImagen_url());
            st.setInt(6, pel.getDestacado());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Pelicula pel) {
        String sql = "UPDATE Pelicula SET titulo=?, duracion_minutos=?, genero=?, sinopsis=?, imagen_url=?, destacado=? WHERE id_pelicula=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, pel.getTitulo());
            st.setInt(2, pel.getDuracion_minutos());
            st.setString(3, pel.getGenero());
            st.setString(4, pel.getSinopsis());
            st.setString(5, pel.getImagen_url());
            st.setInt(6, pel.getDestacado());
            st.setInt(7, pel.getId_pelicula());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Pelicula searchById(int id) {
        Pelicula pel = null;
        String sql = "SELECT * FROM Pelicula WHERE id_pelicula=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) pel = mapping(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pel;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Pelicula WHERE id_pelicula=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Pelicula mapping(ResultSet rs) throws SQLException {
        return new Pelicula(
            rs.getInt("id_pelicula"),
            rs.getString("titulo"),
            rs.getInt("duracion_minutos"),
            rs.getString("genero"),
            rs.getString("sinopsis"),
            rs.getString("imagen_url"),
            rs.getInt("destacado")
        );
    }
}
