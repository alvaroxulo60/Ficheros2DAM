package main;

import exceptions.BBDDException;
import services.ConexionBBDD;

import java.sql.Connection;
import java.sql.SQLException;

public class main {
    static void main(String[] args) {
        try {
            ConexionBBDD c = ConexionBBDD.getInstance();
            Connection con = ConexionBBDD.getConexion();

            if (!con.isClosed()){
                System.out.println("sisisis");
            }
            con.close();
        } catch (BBDDException|SQLException e) {
            System.out.println("nonononono: "+e.getMessage());
        }
    }
}
