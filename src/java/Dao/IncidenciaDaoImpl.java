package Dao;

import Interface.IIncidencia;
import model.*;
import utils.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidenciaDaoImpl implements IIncidencia {

    @Override
    public List<Incidencia> lista() {
        List<Incidencia> lista = new ArrayList<>();
        String sql = "SELECT i.*, s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total, "
                   + "f.id_pelicula, f.id_sala, f.hora_inicio, f.estado as fun_estado, "
                   + "p.titulo, p.duracion_minutos, "
                   + "c.dni, c.nombre as cli_nombre, c.email, c.telefono "
                   + "FROM Incidencia i "
                   + "LEFT JOIN Sala s ON i.id_sala = s.id_sala "
                   + "LEFT JOIN Funcion f ON i.id_funcion = f.id_funcion "
                   + "LEFT JOIN Pelicula p ON f.id_pelicula = p.id_pelicula "
                   + "LEFT JOIN Cliente c ON i.id_cliente = c.id_cliente";
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
    public boolean insertar(Incidencia inc) {
        String sql = "INSERT INTO Incidencia (tipo, descripcion, id_sala, id_funcion, id_cliente, estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, inc.getTipo());
            st.setString(2, inc.getDescripcion());
            if (inc.getSala() != null) st.setInt(3, inc.getSala().getId_sala());
            else st.setNull(3, Types.INTEGER);
            if (inc.getFuncion() != null) st.setInt(4, inc.getFuncion().getId_funcion());
            else st.setNull(4, Types.INTEGER);
            if (inc.getCliente() != null) st.setInt(5, inc.getCliente().getId_cliente());
            else st.setNull(5, Types.INTEGER);
            st.setString(6, inc.getEstado());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Incidencia inc) {
        String sql = "UPDATE Incidencia SET tipo=?, descripcion=?, id_sala=?, id_funcion=?, id_cliente=?, estado=? WHERE id_incidencia=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, inc.getTipo());
            st.setString(2, inc.getDescripcion());
            if (inc.getSala() != null) st.setInt(3, inc.getSala().getId_sala());
            else st.setNull(3, Types.INTEGER);
            if (inc.getFuncion() != null) st.setInt(4, inc.getFuncion().getId_funcion());
            else st.setNull(4, Types.INTEGER);
            if (inc.getCliente() != null) st.setInt(5, inc.getCliente().getId_cliente());
            else st.setNull(5, Types.INTEGER);
            st.setString(6, inc.getEstado());
            st.setInt(7, inc.getId_incidencia());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Incidencia searchById(int id) {
        Incidencia inc = null;
        String sql = "SELECT i.*, s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total, "
                   + "f.id_pelicula, f.hora_inicio, f.estado as fun_estado, "
                   + "p.titulo, p.duracion_minutos, "
                   + "c.dni, c.nombre as cli_nombre, c.email, c.telefono "
                   + "FROM Incidencia i "
                   + "LEFT JOIN Sala s ON i.id_sala = s.id_sala "
                   + "LEFT JOIN Funcion f ON i.id_funcion = f.id_funcion "
                   + "LEFT JOIN Pelicula p ON f.id_pelicula = p.id_pelicula "
                   + "LEFT JOIN Cliente c ON i.id_cliente = c.id_cliente WHERE i.id_incidencia=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) inc = mapping(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inc;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Incidencia WHERE id_incidencia=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Incidencia mapping(ResultSet rs) throws SQLException {
        Sala sala = null;
        try { if (rs.getObject("id_sala") != null) {
            sala = new Sala(rs.getInt("id_sala"), rs.getString("sala_nombre"),
                rs.getString("sala_tipo"), rs.getInt("capacidad_total"));
        }} catch (SQLException e) { e.printStackTrace(); }
        Funcion fun = null;
        try { if (rs.getObject("id_funcion") != null) {
            Pelicula pel = null;
            try { if (rs.getObject("id_pelicula") != null) {
                pel = new Pelicula(rs.getInt("id_pelicula"), rs.getString("titulo"),
                    rs.getInt("duracion_minutos"));
            }} catch (SQLException e) { e.printStackTrace(); }
            fun = new Funcion(rs.getInt("id_funcion"), pel, sala,
                rs.getString("hora_inicio"), rs.getString("fun_estado"));
        }} catch (SQLException e) { e.printStackTrace(); }
        Cliente cli = null;
        try { if (rs.getObject("id_cliente") != null) {
            cli = new Cliente(rs.getInt("id_cliente"), rs.getString("dni"),
                rs.getString("cli_nombre"), rs.getString("email"), rs.getString("telefono"));
        }} catch (SQLException e) { e.printStackTrace(); }
        Incidencia inc = new Incidencia();
        inc.setId_incidencia(rs.getInt("id_incidencia"));
        inc.setTipo(rs.getString("tipo"));
        inc.setDescripcion(rs.getString("descripcion"));
        inc.setSala(sala);
        inc.setFuncion(fun);
        inc.setCliente(cli);
        inc.setFecha_reporte(rs.getTimestamp("fecha_reporte"));
        inc.setEstado(rs.getString("estado"));
        return inc;
    }
}
