package services;

import java.sql.Connection;

public class BBDD {

    Connection conexion;

    public BBDD() {
        this.conexion = ConexionDB.getConnection();
    }
}
