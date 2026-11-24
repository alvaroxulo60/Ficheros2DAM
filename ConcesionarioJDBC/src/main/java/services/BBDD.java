package services;

import exceptions.BBDDException;
import models.Coche;
import models.Extra;
import models.Propietario;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BBDD {
    protected final Connection connection;

    protected BBDD(Connection connection) {
        this.connection = connection;
    }

    public boolean insertPropietario(Propietario propietario)
            throws BBDDException {
        String sql = " INSERT INTO propietarios (dni, nombre, apellidos, telefono) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, propietario.getDni());
            ps.setString(2, propietario.getNombre());
            ps.setString(3, propietario.getApellidos());
            ps.setString(4, propietario.getTelefono());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BBDDException("Error al insertar propietario: " + e.getMessage());
        }
    }

    public abstract void crearTablas() throws BBDDException;

    public boolean insertarCoche(Coche coche) throws BBDDException {
        String sql = "insert into coches (matricula,marca,modelo,extras,precio) values (?,?,?,?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, coche.getMatricula());
            ps.setString(2, coche.getMarca());
            ps.setString(3, coche.getModelo());
            ps.setString(4, formatearExtras(coche.getExtras()));
            ps.setDouble(5, coche.getPrecio());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BBDDException("Error al insertar coche: " + e.getMessage());
        }
    }

    public void insertarDesdeCSV(Path ruta) throws BBDDException {
        String separadorGeneral = ";";
        String separadorExtra = "\\|";

        try (BufferedReader reader = Files.newBufferedReader(ruta)) {
            reader.readLine();

            String linea;

            List<Coche> coches = new ArrayList<>();

            while ((linea = reader.readLine()) != null) {
                String[] valores = linea.split(separadorGeneral);

                String matricula = valores[0];
                String marca = valores[1];
                String modelo = valores[2];
                String extras = valores.length > 3 ? valores[3].trim():"" ;
                double precio = Double.parseDouble(valores[4].trim());



                List<Extra> equipamientos = new ArrayList<>();

                if (!extras.isEmpty()){
                    for (String s : extras.split(separadorExtra)) {
                        equipamientos.add(new Extra(s));
                    }
                }

                Coche c1 = new Coche(matricula, marca, modelo, equipamientos, precio);

                coches.add(c1);

            }
            connection.setAutoCommit(false);
            for (Coche c2 : coches) {
                if (!insertarCoche(c2)) {
                    connection.rollback();
                    break;
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (IOException e) {
            throw new BBDDException("No se ha podido encontrar el archivo csv");
        } catch (SQLException e) {
            throw new BBDDException("");
        }
    }

    public List<Coche> mostrarCochesSinPropietarios() throws BBDDException {
        String sql = "Select * from coches where id_propietario IS NULL";

        return getCoches(sql);
    }

    public List<Coche> mostrarCochesConPropietarios() throws BBDDException {
        String sql = "Select * from coches where id_propietario IS not NULL";

        return getCoches(sql);
    }

    public void generarInformeResumen() throws BBDDException {
        StringBuilder informe = new StringBuilder("INFORME RESUMEN DEL CONCESIONARIO\n\n");
        String sqlTotal = "SELECT COUNT(*) FROM coches";
        String sqlMarcas = "SELECT marca, COUNT(*) AS cantidad FROM coches GROUP BY marca ORDER BY marca";
        String sqlExtras = "SELECT extras FROM coches WHERE extras IS NOT NULL AND extras <> ''";

        try (Statement statement = connection.createStatement();
             ResultSet rsTotal = statement.executeQuery(sqlTotal)) {
            rsTotal.next();
            informe.append("Número total de coches: ").append(rsTotal.getInt(1)).append("\n\n");

            informe.append("Coches por marca:\n");
            try (ResultSet rsMarcas = statement.executeQuery(sqlMarcas)) {
                if (!rsMarcas.isBeforeFirst()) {
                    informe.append("- No hay coches registrados.\n");
                } else {
                    while (rsMarcas.next()) {
                        informe.append("- ").append(rsMarcas.getString("marca"))
                                .append(": ").append(rsMarcas.getInt("cantidad")).append("\n");
                    }
                }
            }

            Map<String, Integer> recuentoExtras = new LinkedHashMap<>();
            try (ResultSet rsExtras = statement.executeQuery(sqlExtras)) {
                while (rsExtras.next()) {
                    String[] extras = rsExtras.getString("extras").split("\\|");
                    for (String extra : extras) {
                        String nombre = extra.trim();
                        if (!nombre.isEmpty()) {
                            recuentoExtras.merge(nombre, 1, Integer::sum);
                        }
                    }
                }
            }

            informe.append("\nExtra o equipamiento más repetido: ");
            if (recuentoExtras.isEmpty()) {
                informe.append("No hay extras registrados.\n");
            } else {
                int maximo = recuentoExtras.values().stream().mapToInt(Integer::intValue).max().orElse(0);
                String masRepetidos = recuentoExtras.entrySet().stream()
                        .filter(entry -> entry.getValue() == maximo)
                        .map(entry -> entry.getKey() + " (" + entry.getValue() + " veces)")
                        .collect(Collectors.joining(", "));
                informe.append(masRepetidos).append("\n");
            }

            Files.writeString(Path.of("informe_concesionario.txt"), informe.toString(), StandardCharsets.UTF_8);
        } catch (SQLException | IOException e) {
            throw new BBDDException("Error al generar el informe del concesionario: " + e.getMessage());
        }
    }

    private List<Coche> getCoches(String sql) throws BBDDException {
        try (Statement stm = connection.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {


            List<Coche> coches = new ArrayList<>();

            while (rs.next()) {
                String matricula = rs.getString(1);
                String marca = rs.getString(2);
                String modelo = rs.getString(3);
                List<Extra> extras = formatearResultSetDeExtras(rs.getString(4));
                double precio = rs.getDouble(5);
                int idPropietario = rs.getInt(6);
                boolean tienePropietario = !rs.wasNull();

                if (tienePropietario){
                    coches.add(new Coche(matricula, marca, modelo, extras, precio, idPropietario));
                }else {
                    coches.add(new Coche(matricula, marca, modelo, extras, precio));
                }
            }

            return coches;
        } catch (SQLException e) {
            throw new BBDDException("Error al listar los coches sin propietarios: " + e.getMessage());
        }
    }


    private String formatearExtras(List<Extra> extras) {
        if (extras == null || extras.isEmpty()) {
            return "";
        }

        return extras.stream()
                .map(Extra::getNombre)
                .collect(Collectors.joining("|"));
    }

    private List<Extra> formatearResultSetDeExtras(String cadena) {

        if (cadena == null || cadena.isEmpty()) {
            return new ArrayList<>();
        }

        String separador = "\\|";

        String[] elementos = cadena.split(separador);

        List<Extra> extras = new ArrayList<>();

        for (String s : elementos) {
            extras.add(new Extra(s));
        }

        return extras;
    }


    public void modificarCoche(Coche c) throws BBDDException {
        String sql = "update coches SET " +
                "marca  = ?," +
                "modelo = ?," +
                "extras = ?," +
                "precio = ? " +
                "where matricula = ?";

        try(PreparedStatement pst = connection.prepareStatement(sql)){
            pst.setString(1,c.getMarca());
            pst.setString(2,c.getModelo());
            pst.setString(3,formatearExtras(c.getExtras()));
            pst.setDouble(4,c.getPrecio());
            pst.setString(5,c.getMatricula());

            int filasAfectadas = pst.executeUpdate();
            if (filasAfectadas == 0){
                throw new BBDDException("La matricula no existe en la base de datos");
            }
        } catch (SQLException e) {
            throw new BBDDException("Error al modificar el coche: "+e.getMessage());
        }
    }

    public void borrarCoche(String matricula) throws BBDDException {

        String borrarTraspaso = "Delete from traspasos where matricula_coche = ?";
        String borrarCoche = "delete from coches where matricula = ?";

        try(PreparedStatement pst1 = connection.prepareStatement(borrarTraspaso);
        PreparedStatement pst2 = connection.prepareStatement(borrarCoche)){
            connection.setAutoCommit(false);

            pst1.setString(1,matricula);

            pst2.setString(1,matricula);


            try {
                pst1.executeUpdate();
                pst2.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new BBDDException("Error al borrar el coche: "+e.getMessage());
            }finally {
                connection.setAutoCommit(true);
            }
        }catch (SQLException e){
            throw new BBDDException("Error al borrar el coche: "+ e.getMessage());
        }
    }

    public void traspasarCoche(String matricula, int idPropietario,int idVendedor, double montoEconomico) throws BBDDException {
        String verificarComprador = "Select * from propietarios where id_propietario = ?";
        String verificarCoche = "Select * from coches where matricula = ?";

        try(PreparedStatement pst1 = connection.prepareStatement(verificarComprador);
        PreparedStatement pst2 = connection.prepareStatement(verificarCoche)){

            pst1.setInt(1,idPropietario);
            pst2.setString(1,matricula);

            if (!pst1.executeQuery().next()){
                throw new BBDDException("No existe ese propietario");
            }
            if (!pst2.executeQuery().next()){
                throw new BBDDException("No existe ese coche");
            }

            String setPropietario = "update coches set " +
                    "id_propietario = ?" +
                    "where matricula = ?";

            String setTraspaso = "Insert into traspasos (matricula_coche,id_vendedor,id_comprador,monto_economico) values (?,?,?,?)";

            try (PreparedStatement pst3 = connection.prepareStatement(setPropietario);
            PreparedStatement pst4 = connection.prepareStatement(setTraspaso)){
                pst3.setInt(1,idPropietario);
                pst3.setString(2,matricula);

                pst4.setString(1,matricula);
                pst4.setInt(2,idVendedor);
                pst4.setInt(3,idPropietario);
                pst4.setDouble(4,montoEconomico);

                connection.setAutoCommit(false);

                try{
                    pst3.executeUpdate();
                    pst4.executeUpdate();
                    connection.commit();
                }catch (SQLException e){
                    connection.rollback();
                    throw new BBDDException("Error al hacer el traspaso: "+e.getMessage());
                }finally {
                    connection.setAutoCommit(true);
                }
            }

        }catch (SQLException e){
            throw new BBDDException("Error en la base de datos: "+e.getMessage());
        }
    }

    public void close() throws SQLException {
        connection.close();
    }
}

