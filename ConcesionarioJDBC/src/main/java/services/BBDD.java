package services;

import exceptions.BBDDException;
import models.Coche;
import models.Extra;
import models.Propietario;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,coche.getMatricula());
            ps.setString(2,coche.getMarca());
            ps.setString(3,coche.getModelo());
            ps.setArray(4,(Array) coche.getExtras());
            ps.setDouble(5,coche.getPrecio());

            return ps.executeUpdate() > 0;
        }catch (SQLException e){
            //todo comprobar como mejorar esto
            //throw new BBDDException("Error al insertar coche: "+e.getMessage());
            return false;
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
                String extras = valores[3];
                double precio = Double.parseDouble(valores[4]);

                String[] equipamientosString = extras.split(separadorExtra);

                List<Extra> equipamientos = new ArrayList<>();

                for (String s : equipamientosString) {
                    equipamientos.add(new Extra(s));
                }

                Coche c1 = new Coche(matricula,marca,modelo,equipamientos,precio);

                coches.add(c1);

            }
            connection.setAutoCommit(false);
            for (Coche c2 : coches) {
                if (!insertarCoche(c2)){
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

    public List<Coche> mostrarCochesSinPropietarios(){
        return new ArrayList<>();
    }
}

