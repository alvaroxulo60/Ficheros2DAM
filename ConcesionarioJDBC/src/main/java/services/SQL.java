package services;

import exceptions.BBDDException;
import models.Propietario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class SQL extends BBDD {

    private ConexionBBDD c;

    public SQL() throws BBDDException, SQLException {
        c = ConexionBBDD.getInstance();
        super(ConexionBBDD.getConexionSQL());
    }

    @Override
    public void crearTablas() throws BBDDException {

        String createSchema = "Create schema if not exists concesionario";
        String useSchema = "use concesionario";


        String createTablePropietarios = "CREATE TABLE IF NOT EXISTS propietarios (" +
                "    id_propietario INT AUTO_INCREMENT PRIMARY KEY," +
                "    dni VARCHAR(10) NOT NULL," +
                "    nombre VARCHAR(100) NOT NULL," +
                "    apellidos VARCHAR(150) NOT NULL," +
                "    telefono VARCHAR(15) NULL" +
                ");";

        String createTableCoches = "CREATE TABLE IF NOT EXISTS coches (" +
                "    matricula VARCHAR(10) PRIMARY KEY," +
                "    marca VARCHAR(50) NOT NULL," +
                "    modelo VARCHAR(50) NOT NULL," +
                "    extras VARCHAR(255) NULL," +
                "    precio DECIMAL(10, 2) NOT NULL," +
                "    id_propietario INT NULL," +
                "    CONSTRAINT fk_coches_propietario" +
                "        FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)" +
                ");";

        String createTableTraspasos = "CREATE TABLE IF NOT EXISTS traspasos (" +
                "    id_traspaso INT AUTO_INCREMENT PRIMARY KEY," +
                "    matricula_coche VARCHAR(10) NOT NULL," +
                "    id_vendedor INT NULL," +
                "    id_comprador INT NOT NULL," +
                "    monto_economico DECIMAL(10, 2) NOT NULL," +
                "    CONSTRAINT fk_traspasos_coche" +
                "        FOREIGN KEY (matricula_coche) REFERENCES coches(matricula)," +
                "    CONSTRAINT fk_traspasos_vendedor" +
                "        FOREIGN KEY (id_vendedor) REFERENCES propietarios(id_propietario)," +
                "    CONSTRAINT fk_traspasos_comprador" +
                "        FOREIGN KEY (id_comprador) REFERENCES propietarios(id_propietario)" +
                ");";


        List<String> sentencias = List.of(
                createSchema,
                useSchema,
                createTablePropietarios,
                createTableCoches,
                createTableTraspasos
        );

        try (Statement statement = connection.createStatement()) {
            for (String sql : sentencias) {
                statement.execute(sql);
            }
        } catch (SQLException e) {
            throw new BBDDException("Error en la bbdd: " + e.getMessage());
        }
    }

}

