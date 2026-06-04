package utils;
import java.sql.*;

public class ConexionSingleton {

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost/db_cinerama"
                       + "?autoReconnect=true&useSSL=false"
                       + "&allowPublicKeyRetrieval=true"
                       + "&connectTimeout=5000&socketTimeout=30000";
            Connection cn = DriverManager.getConnection(url, "root", "admin123");
            if (!cn.isValid(3)) {
                throw new SQLException("La conexion no es valida");
            }
            return cn;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Conexion fallida", e);
        }
    }
}
