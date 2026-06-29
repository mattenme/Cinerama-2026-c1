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
             PreparedStatement st = cn.prepareStatement("SELECT * FROM Pelicula ORDER BY id_pelicula");
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
    public List<Pelicula> listaPaginada(int start, int limit) {
        List<Pelicula> lista = new ArrayList<>();
        String sql = "SELECT * FROM (SELECT p.*, ROWNUM rn FROM (SELECT * FROM Pelicula ORDER BY id_pelicula) p WHERE ROWNUM <= ?) WHERE rn > ?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, start + limit);
            st.setInt(2, start);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) lista.add(mapping(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public int contar() {
        String sql = "SELECT COUNT(*) FROM Pelicula";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean insertar(Pelicula pel) {
        String sql = "INSERT INTO Pelicula (titulo, duracion_minutos, genero, sinopsis, imagen_url, destacado, activo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, pel.getTitulo());
            st.setInt(2, pel.getDuracion_minutos());
            st.setString(3, pel.getGenero());
            st.setString(4, pel.getSinopsis());
            st.setString(5, pel.getImagen_url());
            st.setInt(6, pel.getDestacado());
            st.setInt(7, pel.getActivo());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Pelicula pel) {
        String sql = "UPDATE Pelicula SET titulo=?, duracion_minutos=?, genero=?, sinopsis=?, imagen_url=?, destacado=?, activo=? WHERE id_pelicula=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, pel.getTitulo());
            st.setInt(2, pel.getDuracion_minutos());
            st.setString(3, pel.getGenero());
            st.setString(4, pel.getSinopsis());
            st.setString(5, pel.getImagen_url());
            st.setInt(6, pel.getDestacado());
            st.setInt(7, pel.getActivo());
            st.setInt(8, pel.getId_pelicula());
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
        Connection cn = null;
        PreparedStatement stFuncion = null;
        PreparedStatement stPelicula = null;
        try {
            cn = ConexionSingleton.getConnection();
            cn.setAutoCommit(false);
            stFuncion = cn.prepareStatement("DELETE FROM Funcion WHERE id_pelicula=?");
            stFuncion.setInt(1, id);
            stFuncion.executeUpdate();
            stPelicula = cn.prepareStatement("DELETE FROM Pelicula WHERE id_pelicula=?");
            stPelicula.setInt(1, id);
            int r = stPelicula.executeUpdate();
            cn.commit();
            return r > 0;
        } catch (SQLException e) {
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            try { if (stFuncion != null) stFuncion.close(); } catch (SQLException e) { }
            try { if (stPelicula != null) stPelicula.close(); } catch (SQLException e) { }
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    @Override
    public boolean toggleDestacado(int id) {
        String sql = "UPDATE Pelicula SET destacado = 1 - destacado WHERE id_pelicula=?";
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
    public boolean toggleActivo(int id) {
        String sql = "UPDATE Pelicula SET activo = 1 - activo WHERE id_pelicula=?";
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
        Pelicula p = new Pelicula(
            rs.getInt("id_pelicula"),
            rs.getString("titulo"),
            rs.getInt("duracion_minutos"),
            rs.getString("genero"),
            rs.getString("sinopsis"),
            rs.getString("imagen_url"),
            rs.getInt("destacado"),
            rs.getInt("activo")
        );
        return p;
    }
}
