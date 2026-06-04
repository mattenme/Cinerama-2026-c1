package Dao;

import Interface.ICliente;
import model.Cliente;
import utils.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDaoImpl implements ICliente {

    @Override
    public List<Cliente> lista() {
        List<Cliente> lista = new ArrayList<>();
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement("SELECT * FROM Cliente");
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
    public int insertar(Cliente cli) {
        String sql = "INSERT INTO Cliente (dni, nombre, email, telefono, avatar_url, cancelaciones_acumuladas, es_frecuente) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, cli.getDni());
            st.setString(2, cli.getNombre());
            st.setString(3, cli.getEmail());
            st.setString(4, cli.getTelefono());
            st.setString(5, cli.getAvatar_url());
            st.setInt(6, cli.getCancelaciones_acumuladas());
            st.setBoolean(7, cli.isEs_frecuente());
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
    public boolean update(Cliente cli) {
        String sql = "UPDATE Cliente SET dni=?, nombre=?, email=?, telefono=?, avatar_url=?, cancelaciones_acumuladas=?, es_frecuente=? WHERE id_cliente=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, cli.getDni());
            st.setString(2, cli.getNombre());
            st.setString(3, cli.getEmail());
            st.setString(4, cli.getTelefono());
            st.setString(5, cli.getAvatar_url());
            st.setInt(6, cli.getCancelaciones_acumuladas());
            st.setBoolean(7, cli.isEs_frecuente());
            st.setInt(8, cli.getId_cliente());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Cliente searchById(int id) {
        Cliente cli = null;
        String sql = "SELECT * FROM Cliente WHERE id_cliente=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) cli = mapping(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cli;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Cliente WHERE id_cliente=?";
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
    public Cliente searchByDni(String dni) {
        Cliente cli = null;
        String sql = "SELECT * FROM Cliente WHERE dni=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, dni);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) cli = mapping(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cli;
    }

    private Cliente mapping(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getInt("id_cliente"),
            rs.getString("dni"),
            rs.getString("nombre"),
            rs.getString("email"),
            rs.getString("telefono"),
            rs.getString("avatar_url"),
            rs.getInt("cancelaciones_acumuladas"),
            rs.getBoolean("es_frecuente")
        );
    }
}
