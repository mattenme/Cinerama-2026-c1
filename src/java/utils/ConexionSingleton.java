package utils;
import java.sql.*;

public class ConexionSingleton {

    public static Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            String url = "jdbc:oracle:thin:@localhost:1521:XE";
            Connection cn = DriverManager.getConnection(url, "cineramaBD", "123");
            if (!cn.isValid(3)) {
                throw new SQLException("La conexion no es valida");
            }
            return cn;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Conexion fallida", e);
        }
    }
}
