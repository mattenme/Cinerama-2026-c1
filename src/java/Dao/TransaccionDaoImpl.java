package Dao;

import Interface.ITransaccion;
import model.*;
import utils.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaccionDaoImpl implements ITransaccion {

    @Override
    public List<Transaccion> lista() {
        List<Transaccion> lista = new ArrayList<>();
        String sql = "SELECT t.*, c.dni, c.nombre as cli_nombre, c.cancelaciones_acumuladas, c.es_frecuente, "
                   + "f.id_pelicula, f.id_sala, f.hora_inicio, f.estado as fun_estado, "
                   + "p.titulo, p.duracion_minutos, "
                   + "s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total "
                   + "FROM Transaccion t "
                   + "JOIN Cliente c ON t.id_cliente = c.id_cliente "
                   + "JOIN Funcion f ON t.id_funcion = f.id_funcion "
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
    public int insertar(Transaccion trans) {
        String sql = "INSERT INTO Transaccion (id_cliente, id_funcion, monto_total, metodo_pago, estado, codigo_qr) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, trans.getCliente().getId_cliente());
            st.setInt(2, trans.getFuncion().getId_funcion());
            st.setDouble(3, trans.getMonto_total());
            st.setString(4, trans.getMetodo_pago());
            st.setString(5, trans.getEstado());
            st.setString(6, trans.getCodigo_qr());
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
    public Transaccion searchById(int id) {
        Transaccion trans = null;
        String sql = "SELECT t.*, c.dni, c.nombre as cli_nombre, c.cancelaciones_acumuladas, c.es_frecuente, "
                   + "f.id_pelicula, f.id_sala, f.hora_inicio, f.estado as fun_estado, "
                   + "p.titulo, p.duracion_minutos, "
                   + "s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total "
                   + "FROM Transaccion t "
                   + "JOIN Cliente c ON t.id_cliente = c.id_cliente "
                   + "JOIN Funcion f ON t.id_funcion = f.id_funcion "
                   + "JOIN Pelicula p ON f.id_pelicula = p.id_pelicula "
                   + "JOIN Sala s ON f.id_sala = s.id_sala WHERE t.id_transaccion=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) trans = mapping(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trans;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Transaccion WHERE id_transaccion=?";
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
    public List<Transaccion> listarPorCliente(int idCliente) {
        List<Transaccion> lista = new ArrayList<>();
        String sql = "SELECT t.*, c.dni, c.nombre as cli_nombre, c.cancelaciones_acumuladas, c.es_frecuente, "
                   + "f.id_pelicula, f.id_sala, f.hora_inicio, f.estado as fun_estado, "
                   + "p.titulo, p.duracion_minutos, "
                   + "s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total "
                   + "FROM Transaccion t "
                   + "JOIN Cliente c ON t.id_cliente = c.id_cliente "
                   + "JOIN Funcion f ON t.id_funcion = f.id_funcion "
                   + "JOIN Pelicula p ON f.id_pelicula = p.id_pelicula "
                   + "JOIN Sala s ON f.id_sala = s.id_sala WHERE t.id_cliente=?";
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

    private Transaccion mapping(ResultSet rs) throws SQLException {
        Cliente cli = new Cliente(
            rs.getInt("id_cliente"),
            rs.getString("dni"),
            rs.getString("cli_nombre"),
            rs.getInt("cancelaciones_acumuladas"),
            rs.getBoolean("es_frecuente")
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
        return new Transaccion(
            rs.getInt("id_transaccion"),
            cli,
            fun,
            rs.getDouble("monto_total"),
            rs.getString("metodo_pago"),
            rs.getString("estado"),
            rs.getString("codigo_qr"),
            rs.getTimestamp("fecha_transaccion")
        );
    }
}
