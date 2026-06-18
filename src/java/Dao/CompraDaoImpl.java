package Dao;

import Interface.ICompra;
import model.*;
import utils.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompraDaoImpl implements ICompra {

    @Override
    public List<Compra> lista() {
        List<Compra> lista = new ArrayList<>();
        String sql = "SELECT c.*, cl.dni, cl.nombre as cli_nombre, cl.email, cl.telefono, "
                   + "f.id_pelicula, f.id_sala, f.hora_inicio, f.estado as fun_estado, "
                   + "p.titulo, p.duracion_minutos, "
                   + "s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total, "
                   + "b.fila, b.numero, b.estado as but_estado "
                   + "FROM Compra c "
                   + "JOIN Cliente cl ON c.id_cliente = cl.id_cliente "
                   + "JOIN Funcion f ON c.id_funcion = f.id_funcion "
                   + "JOIN Pelicula p ON f.id_pelicula = p.id_pelicula "
                   + "JOIN Sala s ON f.id_sala = s.id_sala "
                   + "JOIN Butaca b ON c.id_butaca = b.id_butaca";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql);
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
    public int insertar(Compra compra) {
        String sql = "INSERT INTO Compra (id_cliente, id_funcion, id_butaca, precio, metodo_pago, estado, codigo_qr, productos) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        PreparedStatement up = null;
        try {
            cn = ConexionSingleton.getConnection();
            cn.setAutoCommit(false);
            st = cn.prepareStatement(sql, new String[]{"id_compra"});
            st.setInt(1, compra.getCliente().getId_cliente());
            st.setInt(2, compra.getFuncion().getId_funcion());
            st.setInt(3, compra.getButaca().getId_butaca());
            st.setDouble(4, compra.getPrecio());
            st.setString(5, compra.getMetodo_pago());
            st.setString(6, compra.getEstado());
            st.setString(7, compra.getCodigo_qr());
            st.setString(8, compra.getProductos());
            st.executeUpdate();
            rs = st.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("No se gener\u00F3 ID de compra");
            }
            int id = rs.getInt(1);
            rs.close();
            up = cn.prepareStatement("UPDATE Butaca SET estado='Vendida' WHERE id_butaca=? AND estado IN ('Disponible','Seleccionada')");
            up.setInt(1, compra.getButaca().getId_butaca());
            int rows = up.executeUpdate();
            if (rows == 0) {
                throw new SQLException("La butaca ya est\u00E1 vendida o fue tomada por otro usuario");
            }
            up.close();
            cn.commit();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { }
            try { if (st != null) st.close(); } catch (SQLException e) { }
            try { if (up != null) up.close(); } catch (SQLException e) { }
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return 0;
    }

    @Override
    public Compra searchById(int id) {
        Compra compra = null;
        String sql = "SELECT c.*, cl.dni, cl.nombre as cli_nombre, cl.email, cl.telefono, "
                   + "f.id_pelicula, f.id_sala, f.hora_inicio, f.estado as fun_estado, "
                   + "p.titulo, p.duracion_minutos, "
                   + "s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total, "
                   + "b.fila, b.numero, b.estado as but_estado "
                   + "FROM Compra c "
                   + "JOIN Cliente cl ON c.id_cliente = cl.id_cliente "
                   + "JOIN Funcion f ON c.id_funcion = f.id_funcion "
                   + "JOIN Pelicula p ON f.id_pelicula = p.id_pelicula "
                   + "JOIN Sala s ON f.id_sala = s.id_sala "
                   + "JOIN Butaca b ON c.id_butaca = b.id_butaca WHERE c.id_compra=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    compra = mapping(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return compra;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Compra WHERE id_compra=?";
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
    public List<Compra> listarPorCliente(int idCliente) {
        List<Compra> lista = new ArrayList<>();
        String sql = "SELECT c.*, cl.dni, cl.nombre as cli_nombre, cl.email, cl.telefono, "
                   + "f.id_pelicula, f.id_sala, f.hora_inicio, f.estado as fun_estado, "
                   + "p.titulo, p.duracion_minutos, "
                   + "s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total, "
                   + "b.fila, b.numero, b.estado as but_estado "
                   + "FROM Compra c "
                   + "JOIN Cliente cl ON c.id_cliente = cl.id_cliente "
                   + "JOIN Funcion f ON c.id_funcion = f.id_funcion "
                   + "JOIN Pelicula p ON f.id_pelicula = p.id_pelicula "
                   + "JOIN Sala s ON f.id_sala = s.id_sala "
                   + "JOIN Butaca b ON c.id_butaca = b.id_butaca WHERE c.id_cliente=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, idCliente);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapping(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Compra mapping(ResultSet rs) throws SQLException {
        Cliente cli = new Cliente(
            rs.getInt("id_cliente"),
            rs.getString("dni"),
            rs.getString("cli_nombre"),
            rs.getString("email"),
            rs.getString("telefono")
        );
        Pelicula pel = new Pelicula(
            rs.getInt("id_pelicula"),
            rs.getString("titulo"),
            rs.getInt("duracion_minutos")
        );
        Sala sala = new Sala(
            rs.getInt("id_sala"),
            rs.getString("sala_nombre"),
            rs.getString("sala_tipo"),
            rs.getInt("capacidad_total")
        );
        Funcion fun = new Funcion(
            rs.getInt("id_funcion"),
            pel,
            sala,
            rs.getString("hora_inicio"),
            rs.getString("fun_estado")
        );
        Butaca but = new Butaca(
            rs.getInt("id_butaca"),
            sala,
            rs.getString("fila"),
            rs.getInt("numero"),
            rs.getString("but_estado")
        );
        Compra c = new Compra(
            rs.getInt("id_compra"),
            cli,
            fun,
            but,
            rs.getDouble("precio"),
            rs.getString("metodo_pago"),
            rs.getString("estado"),
            rs.getString("codigo_qr"),
            rs.getTimestamp("fecha_compra")
        );
        c.setProductos(rs.getString("productos"));
        return c;
    }
}
