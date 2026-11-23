package services;

import exceptions.AppException;
import exceptions.BBDDException;
import io.PropertiesReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBBDD {
    PropertiesReader p;

    private static String url;
    private static String user;
    private static String pass;
    private static String urlLite;

    private static ConexionBBDD instance;

    private ConexionBBDD() throws BBDDException {
        try {
            p = PropertiesReader.getInstance();
            url = p.get("db.URL");
            user = p.get("db.USER");
            pass = p.get("db.PASS");
            urlLite = p.get("lite.URL");

        } catch (AppException e) {
            throw new BBDDException("Error en la conexion: "+ e.getMessage());
        }
    }

    public static ConexionBBDD getInstance() throws BBDDException {
        if (instance == null){
                instance = new ConexionBBDD();
        }
        return instance;
    }

    public static Connection getConexionSQL() throws SQLException {
        return DriverManager.getConnection(url,user,pass);
    }

    public static Connection getConexionLite() throws SQLException {
        return DriverManager.getConnection(urlLite);
    }
}
