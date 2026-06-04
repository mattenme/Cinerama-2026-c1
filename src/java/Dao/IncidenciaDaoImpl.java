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
                   + "f.id_pelicula, f.hora_inicio, f.estado as fun_estado, "
                   + "p.titulo, p.duracion_minutos "
                   + "FROM Incidencia i "
                   + "LEFT JOIN Sala s ON i.id_sala = s.id_sala "
                   + "LEFT JOIN Funcion f ON i.id_funcion = f.id_funcion "
                   + "LEFT JOIN Pelicula p ON f.id_pelicula = p.id_pelicula";
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
        String sql = "INSERT INTO Incidencia (tipo, id_sala, id_funcion, reportado_por, estado) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, inc.getTipo());
            if (inc.getSala() != null) st.setInt(2, inc.getSala().getId_sala());
            else st.setNull(2, Types.INTEGER);
            if (inc.getFuncion() != null) st.setInt(3, inc.getFuncion().getId_funcion());
            else st.setNull(3, Types.INTEGER);
            st.setString(4, inc.getReportado_por());
            st.setString(5, inc.getEstado());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Incidencia inc) {
        String sql = "UPDATE Incidencia SET tipo=?, id_sala=?, id_funcion=?, reportado_por=?, estado=? WHERE id_incidencia=?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, inc.getTipo());
            if (inc.getSala() != null) st.setInt(2, inc.getSala().getId_sala());
            else st.setNull(2, Types.INTEGER);
            if (inc.getFuncion() != null) st.setInt(3, inc.getFuncion().getId_funcion());
            else st.setNull(3, Types.INTEGER);
            st.setString(4, inc.getReportado_por());
            st.setString(5, inc.getEstado());
            st.setInt(6, inc.getId_incidencia());
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
                   + "p.titulo, p.duracion_minutos "
                   + "FROM Incidencia i "
                   + "LEFT JOIN Sala s ON i.id_sala = s.id_sala "
                   + "LEFT JOIN Funcion f ON i.id_funcion = f.id_funcion "
                   + "LEFT JOIN Pelicula p ON f.id_pelicula = p.id_pelicula WHERE i.id_incidencia=?";
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
        Incidencia inc = new Incidencia();
        inc.setId_incidencia(rs.getInt("id_incidencia"));
        inc.setTipo(rs.getString("tipo"));
        inc.setSala(sala);
        inc.setFuncion(fun);
        inc.setFecha_reporte(rs.getTimestamp("fecha_reporte"));
        inc.setReportado_por(rs.getString("reportado_por"));
        inc.setEstado(rs.getString("estado"));
        return inc;
    }
}
