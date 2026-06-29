package Dao;

import Interface.IFuncion;
import model.*;
import utils.ConexionSingleton;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FuncionDaoImpl implements IFuncion {

    @Override
    public List<Funcion> lista() {
        List<Funcion> lista = new ArrayList<>();
        String sql = "SELECT f.id_funcion, f.id_pelicula, f.id_sala, "
                   + "TO_CHAR(f.hora_inicio, 'YYYY-MM-DD\"T\"HH24:MI:SS') as hora_inicio, f.estado, f.activo, "
                   + "p.titulo, p.duracion_minutos, "
                   + "s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total as sala_capacidad, s.activo as sala_activo "
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
        String sql = "INSERT INTO Funcion (id_pelicula, id_sala, hora_inicio, estado, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, fun.getPelicula().getId_pelicula());
            st.setInt(2, fun.getSala().getId_sala());
            java.sql.Timestamp ts = parseHora(fun.getHora_inicio());
            if (ts == null) return false;
            st.setTimestamp(3, ts);
            st.setString(4, fun.getEstado());
            st.setInt(5, fun.getActivo());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Funcion fun) {
        String sql = "UPDATE Funcion SET id_pelicula=?, id_sala=?, hora_inicio=?, estado=?, activo=? WHERE id_funcion=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, fun.getPelicula().getId_pelicula());
            st.setInt(2, fun.getSala().getId_sala());
            java.sql.Timestamp ts = parseHora(fun.getHora_inicio());
            if (ts == null) return false;
            st.setTimestamp(3, ts);
            st.setString(4, fun.getEstado());
            st.setInt(5, fun.getActivo());
            st.setInt(6, fun.getId_funcion());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private java.sql.Timestamp parseHora(String hora) {
        if (hora == null || hora.isEmpty()) return null;
        try {
            LocalDateTime dt = LocalDateTime.parse(hora, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return java.sql.Timestamp.valueOf(dt);
        } catch (Exception e) {
            try {
                LocalDateTime dt = LocalDateTime.parse(hora, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                return java.sql.Timestamp.valueOf(dt);
            } catch (Exception e2) {
                e2.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public Funcion searchById(int id) {
        Funcion fun = null;
        String sql = "SELECT f.id_funcion, f.id_pelicula, f.id_sala, "
                   + "TO_CHAR(f.hora_inicio, 'YYYY-MM-DD\"T\"HH24:MI:SS') as hora_inicio, f.estado, f.activo, "
                   + "p.titulo, p.duracion_minutos, "
                   + "s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total as sala_capacidad, s.activo as sala_activo "
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
        Connection cn = null;
        PreparedStatement st1 = null, st2 = null, st3 = null;
        try {
            cn = ConexionSingleton.getConnection();
            cn.setAutoCommit(false);
            st1 = cn.prepareStatement("DELETE FROM Incidencia WHERE id_funcion=?");
            st1.setInt(1, id);
            st1.executeUpdate();
            st2 = cn.prepareStatement("DELETE FROM Compra WHERE id_funcion=?");
            st2.setInt(1, id);
            st2.executeUpdate();
            st3 = cn.prepareStatement("DELETE FROM Funcion WHERE id_funcion=?");
            st3.setInt(1, id);
            int r = st3.executeUpdate();
            cn.commit();
            return r > 0;
        } catch (SQLException e) {
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            try { if (st1 != null) st1.close(); } catch (SQLException e) { }
            try { if (st2 != null) st2.close(); } catch (SQLException e) { }
            try { if (st3 != null) st3.close(); } catch (SQLException e) { }
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    @Override
    public List<Funcion> listarPorPelicula(int idPelicula) {
        List<Funcion> lista = new ArrayList<>();
        String sql = "SELECT f.id_funcion, f.id_pelicula, f.id_sala, "
                   + "TO_CHAR(f.hora_inicio, 'YYYY-MM-DD\"T\"HH24:MI:SS') as hora_inicio, f.estado, f.activo, "
                   + "p.titulo, p.duracion_minutos, "
                   + "s.nombre as sala_nombre, s.tipo as sala_tipo, s.capacidad_total as sala_capacidad, s.activo as sala_activo "
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

    @Override
    public boolean toggleActivo(int id) {
        String sql = "UPDATE Funcion SET activo = 1 - activo WHERE id_funcion=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
            rs.getInt("sala_capacidad"),
            rs.getInt("sala_activo")
        );
        return new Funcion(
            rs.getInt("id_funcion"),
            pel,
            sala,
            rs.getString("hora_inicio"),
            rs.getString("estado"),
            rs.getInt("activo")
        );
    }
}
