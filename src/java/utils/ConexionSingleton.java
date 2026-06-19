package utils;
import java.sql.*;

public class ConexionSingleton {

    public static Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            String url = System.getProperty("db.url", "jdbc:oracle:thin:@localhost:1521:XE");
            String user = System.getProperty("db.user", "cineramaBD");
            String pass = System.getProperty("db.password", "123");
            Connection cn = DriverManager.getConnection(url, user, pass);
            if (!cn.isValid(3)) {
                throw new SQLException("La conexion no es valida");
            }
            return cn;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Conexion fallida", e);
        }
    }
}
