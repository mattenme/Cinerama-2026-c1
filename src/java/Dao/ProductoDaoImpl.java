package Dao;

import Interface.IProducto;
import model.Producto;
import utils.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDaoImpl implements IProducto {

    @Override
    public List<Producto> lista() {
        List<Producto> lista = new ArrayList<>();
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement("SELECT * FROM Producto ORDER BY id_producto");
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapping(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public int insertar(Producto prod) {
        String sql = "INSERT INTO Producto (nombre, descripcion, precio, imagen_url, categoria, activo) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql, new String[]{"id_producto"})) {
            st.setString(1, prod.getNombre());
            st.setString(2, prod.getDescripcion());
            st.setDouble(3, prod.getPrecio());
            st.setString(4, prod.getImagen_url());
            st.setString(5, prod.getCategoria());
            st.setInt(6, prod.isActivo() ? 1 : 0);
            st.executeUpdate();
            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean update(Producto prod) {
        String sql = "UPDATE Producto SET nombre=?, descripcion=?, precio=?, imagen_url=?, categoria=?, activo=? WHERE id_producto=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, prod.getNombre());
            st.setString(2, prod.getDescripcion());
            st.setDouble(3, prod.getPrecio());
            st.setString(4, prod.getImagen_url());
            st.setString(5, prod.getCategoria());
            st.setInt(6, prod.isActivo() ? 1 : 0);
            st.setInt(7, prod.getId_producto());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Producto searchById(int id) {
        Producto prod = null;
        String sql = "SELECT * FROM Producto WHERE id_producto=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) prod = mapping(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prod;
    }

    @Override
    public boolean delete(int id) {
        Connection cn = null;
        PreparedStatement st = null;
        try {
            cn = ConexionSingleton.getConnection();
            cn.setAutoCommit(false);
            st = cn.prepareStatement("DELETE FROM Producto WHERE id_producto=?");
            st.setInt(1, id);
            int r = st.executeUpdate();
            cn.commit();
            return r > 0;
        } catch (SQLException e) {
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            try { if (st != null) st.close(); } catch (SQLException e) { }
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    @Override
    public boolean toggleActivo(int id) {
        String sql = "UPDATE Producto SET activo = 1 - activo WHERE id_producto=?";
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
    public List<Producto> listarActivos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE activo=1 ORDER BY id_producto";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapping(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Producto mapping(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt("id_producto"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            rs.getDouble("precio"),
            rs.getString("imagen_url"),
            rs.getString("categoria"),
            rs.getInt("activo") == 1
        );
    }
}
