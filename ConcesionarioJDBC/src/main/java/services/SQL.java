package services;

import exceptions.BBDDException;
import models.Coche;
import models.Extra;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class SQL extends BBDD {

    private ConexionBBDD c;

    /** Abre la conexión con MySQL. */
    public SQL() throws BBDDException, SQLException {
        c = ConexionBBDD.getInstance();
        super(ConexionBBDD.getConexionSQL());
    }

    /** Crea las tablas y el procedimiento de búsqueda en MySQL. */
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

        String dropProcedure = "drop procedure if exists sp_coches_por_marca";

        String createProcedure = """
                CREATE PROCEDURE sp_coches_por_marca(IN p_marca VARCHAR(50))
                BEGIN
                    SELECT matricula, marca, modelo, extras, precio, id_propietario
                    FROM coches
                    WHERE marca = p_marca;
                END
                """;


        List<String> sentencias = List.of(
                createSchema,
                useSchema,
                createTablePropietarios,
                createTableCoches,
                createTableTraspasos,
                dropProcedure,
                createProcedure
        );

        try (Statement statement = connection.createStatement()) {
            for (String sql : sentencias) {
                statement.execute(sql);
            }
        } catch (SQLException e) {
            throw new BBDDException("Error en la bbdd: " + e.getMessage());
        }
    }

    /**
     * Metodo para ejecutar un procedimiento almacenado en la bbdd
     * @param marcaABuscar marca de coches a buscar
     * @return una lista con los coches con marca coincidentes
     * @throws BBDDException
     */
    public List<Coche> ejecutarProcedimiento(String marcaABuscar) throws BBDDException {
        String sql = "{CALL sp_coches_por_marca(?)}";

        try (CallableStatement cs = super.connection.prepareCall(sql)) {
            cs.setString(1, marcaABuscar);
            List<Coche> coches = new ArrayList<>();
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    String matricula = rs.getString(1);
                    String marca = rs.getString(2);
                    String modelo = rs.getString(3);
                    List<Extra> extras = formatearResultSetDeExtras(rs.getString(4));
                    double precio = rs.getDouble(5);
                    int idPropietario = rs.getInt(6);
                    boolean tienePropietario = !rs.wasNull();

                    if (tienePropietario) {
                        coches.add(new Coche(matricula, marca, modelo, extras, precio, idPropietario));
                    } else {
                        coches.add(new Coche(matricula, marca, modelo, extras, precio));
                    }
                }
                return coches;
            }
        } catch (SQLException e) {
            throw new BBDDException("Error al hacer el procedimiento");
        }
    }

}

