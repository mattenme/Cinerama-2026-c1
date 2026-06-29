package Dao;

import Interface.ICliente;
import model.Cliente;
import utils.BCrypt;
import utils.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDaoImpl implements ICliente {

    @Override
    public List<Cliente> lista() {
        List<Cliente> lista = new ArrayList<>();
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement("SELECT * FROM Cliente ORDER BY id_cliente");
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) lista.add(mapping(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public int insertar(Cliente cli) {
        String sql = "INSERT INTO Cliente (dni, nombre, email, telefono, contrasena, rol, activo, verificado, codigo_verificacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql, new String[]{"id_cliente"})) {
            st.setString(1, cli.getDni());
            st.setString(2, cli.getNombre());
            st.setString(3, cli.getEmail());
            st.setString(4, cli.getTelefono());
            String hash = BCrypt.hashpw(cli.getContrasena(), BCrypt.gensalt());
            st.setString(5, hash);
            st.setString(6, cli.getRol());
            st.setInt(7, cli.getActivo());
            st.setInt(8, cli.getVerificado());
            st.setString(9, cli.getCodigoVerificacion());
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
        String sql = "UPDATE Cliente SET dni=?, nombre=?, email=?, telefono=?, contrasena=?, rol=?, activo=?, verificado=?, codigo_verificacion=? WHERE id_cliente=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, cli.getDni());
            st.setString(2, cli.getNombre());
            st.setString(3, cli.getEmail());
            st.setString(4, cli.getTelefono());
            String pass = cli.getContrasena();
            if (pass != null && !pass.trim().isEmpty()) {
                pass = BCrypt.hashpw(pass, BCrypt.gensalt());
            }
            st.setString(5, pass);
            st.setString(6, cli.getRol());
            st.setInt(7, cli.getActivo());
            st.setInt(8, cli.getVerificado());
            st.setString(9, cli.getCodigoVerificacion());
            st.setInt(10, cli.getId_cliente());
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
        Connection cn = null;
        PreparedStatement st1 = null, st2 = null, st3 = null, st4 = null;
        try {
            cn = ConexionSingleton.getConnection();
            cn.setAutoCommit(false);
            st1 = cn.prepareStatement("DELETE FROM Incidencia WHERE id_cliente=?");
            st1.setInt(1, id);
            st1.executeUpdate();
            st2 = cn.prepareStatement("DELETE FROM Calificacion WHERE id_cliente=?");
            st2.setInt(1, id);
            st2.executeUpdate();
            st3 = cn.prepareStatement("DELETE FROM Compra WHERE id_cliente=?");
            st3.setInt(1, id);
            st3.executeUpdate();
            st4 = cn.prepareStatement("DELETE FROM Cliente WHERE id_cliente=?");
            st4.setInt(1, id);
            int rows = st4.executeUpdate();
            cn.commit();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            try { if (st1 != null) st1.close(); } catch (SQLException e) { }
            try { if (st2 != null) st2.close(); } catch (SQLException e) { }
            try { if (st3 != null) st3.close(); } catch (SQLException e) { }
            try { if (st4 != null) st4.close(); } catch (SQLException e) { }
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
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

    @Override
    public Cliente autenticar(String dni, String contrasena) {
        Cliente cli = searchByDni(dni);
        if (cli == null) return null;
        String hashGuardado = cli.getContrasena();
        if (hashGuardado == null) {
            if (cli.getDni().equals(contrasena)) {
                String hash = BCrypt.hashpw(contrasena, BCrypt.gensalt());
                cli.setContrasena(hash);
                String sql = "UPDATE Cliente SET contrasena=? WHERE id_cliente=?";
                try (Connection cn = ConexionSingleton.getConnection();
                     PreparedStatement st = cn.prepareStatement(sql)) {
                    st.setString(1, hash);
                    st.setInt(2, cli.getId_cliente());
                    st.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return cli;
            }
            return null;
        }
        if (BCrypt.checkpw(contrasena, hashGuardado)) return cli;
        return null;
    }

    @Override
    public boolean toggleActivo(int id) {
        String sql = "UPDATE Cliente SET activo = 1 - activo WHERE id_cliente=?";
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
    public boolean guardarCodigoVerificacion(int idCliente, String codigo) {
        String sql = "UPDATE Cliente SET codigo_verificacion=? WHERE id_cliente=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, codigo);
            st.setInt(2, idCliente);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean existeAdmin() {
        String sql = "SELECT COUNT(*) FROM Cliente WHERE rol='admin'";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean verificarCliente(int idCliente) {
        String sql = "UPDATE Cliente SET verificado=1, codigo_verificacion=NULL WHERE id_cliente=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, idCliente);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Cliente mapping(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId_cliente(rs.getInt("id_cliente"));
        c.setDni(rs.getString("dni"));
        c.setNombre(rs.getString("nombre"));
        c.setEmail(rs.getString("email"));
        c.setTelefono(rs.getString("telefono"));
        try { c.setContrasena(rs.getString("contrasena")); } catch (SQLException e) { }
        try { c.setRol(rs.getString("rol")); } catch (SQLException e) { }
        if (c.getRol() == null) c.setRol("cliente");
        try { c.setActivo(rs.getInt("activo")); } catch (SQLException e) { }
        try { c.setVerificado(rs.getInt("verificado")); } catch (SQLException e) { }
        try { c.setCodigoVerificacion(rs.getString("codigo_verificacion")); } catch (SQLException e) { }
        return c;
    }
}
