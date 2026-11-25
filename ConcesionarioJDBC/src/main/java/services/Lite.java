package services;

import exceptions.BBDDException;
import models.Propietario;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Lite extends BBDD {
    ConexionBBDD c;

    /** Abre la conexión con SQLite. */
    public Lite() throws BBDDException, SQLException {
        c = ConexionBBDD.getInstance();
        super(ConexionBBDD.getConexionLite());
    }

    /** Crea las tablas necesarias en SQLite si todavía no existen. */
    @Override
    public void crearTablas() throws BBDDException {
        String enableForeignKeys = "PRAGMA foreign_keys = ON";

        String createTablePropietarios = "CREATE TABLE IF NOT EXISTS propietarios (" +
                "    id_propietario INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    dni VARCHAR(10) NOT NULL," +
                "    nombre VARCHAR(100) NOT NULL," +
                "    apellidos VARCHAR(150) NOT NULL," +
                "    telefono VARCHAR(15) NULL" +
                ")";

        String createTableCoches = "CREATE TABLE IF NOT EXISTS coches (" +
                "    matricula VARCHAR(10) PRIMARY KEY," +
                "    marca VARCHAR(50) NOT NULL," +
                "    modelo VARCHAR(50) NOT NULL," +
                "    extras VARCHAR(255) NULL," +
                "    precio NUMERIC(10, 2) NOT NULL," +
                "    id_propietario INTEGER NULL," +
                "    CONSTRAINT fk_coches_propietario" +
                "        FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)" +
                ")";

        String createTableTraspasos = "CREATE TABLE IF NOT EXISTS traspasos (" +
                "    id_traspaso INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    matricula_coche VARCHAR(10) NOT NULL," +
                "    id_vendedor INTEGER NULL," +
                "    id_comprador INTEGER NOT NULL," +
                "    monto_economico NUMERIC(10, 2) NOT NULL," +
                "    CONSTRAINT fk_traspasos_coche" +
                "        FOREIGN KEY (matricula_coche) REFERENCES coches(matricula)," +
                "    CONSTRAINT fk_traspasos_vendedor" +
                "        FOREIGN KEY (id_vendedor) REFERENCES propietarios(id_propietario)," +
                "    CONSTRAINT fk_traspasos_comprador" +
                "        FOREIGN KEY (id_comprador) REFERENCES propietarios(id_propietario)" +
                ")";

        List<String> sentencias = List.of(
                enableForeignKeys,
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
