package main;

import io.PropertiesReader;
import models.BBDD;
import io.MiEntradaSalida;
import exception.AppException;
import exception.BBDDException;
import exception.MiEntrdaSalidaException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class main {

    public static void main(String[] args) {
        BBDD baseDatos = inicializarBBDD();

        if (baseDatos == null) {
            System.out.println("El programa no puede continuar sin inicializar la base de datos.");
            return;
        }

        boolean salir = false;

        while (!salir) {
            mostrarMenu();
            try {
                // Usamos leerEnteroRango porque controla la excepción MiEntrdaSalidaException
                // Ahora el rango es de 0 a 7
                int opcion = MiEntradaSalida.leerEnteroRango("Seleccione una opción: ", 0, 7);

                switch (opcion) {
                    case 1: // Insertar
                        int posInsertar = MiEntradaSalida.leerEntero("Introduce la posición donde insertar (-1 para añadir al final): ");
                        String matricula = MiEntradaSalida.leerLinea("Introduce la matrícula: \n");
                        String marca = MiEntradaSalida.leerLinea("Introduce la marca: \n");
                        String modelo = MiEntradaSalida.leerLinea("Introduce la modelo: \n");
                        baseDatos.insertar(posInsertar, matricula, marca, modelo);
                        System.out.println("Registro insertado correctamente.");
                        break;

                    case 2: // Buscar
                        String matriculaBuscar = MiEntradaSalida.leerLinea("Introduce la matrícula a buscar: ");
                        int posicionBuscada = baseDatos.buscar(matriculaBuscar);
                        if (posicionBuscada != -1) {
                            System.out.println("Registro encontrado en la posición: " + posicionBuscada);
                        } else {
                            System.out.println("No se ha encontrado ninguna coincidencia para esa matrícula.");
                        }
                        break;

                    case 3: // Borrar
                        int posBorrar = MiEntradaSalida.leerEntero("Introduce la posición del registro a borrar: ");
                        baseDatos.borrar(posBorrar);
                        System.out.println("Registro borrado correctamente.");
                        break;

                    case 4: // Modificar (NUEVO MÉTODO)
                        int posModificar = MiEntradaSalida.leerEntero("Introduce la posición del registro a modificar: ");
                        String marcaMod = MiEntradaSalida.leerLinea("Introduce la nueva marca: \n");
                        String modeloMod = MiEntradaSalida.leerLinea("Introduce el nuevo modelo:  \n");
                        baseDatos.modificarRegistro(posModificar, marcaMod, modeloMod);
                        System.out.println("Registro modificado correctamente.");
                        break;

                    case 5: // Ordenar
                        baseDatos.ordenarPorMatricula();
                        System.out.println("Base de datos ordenada por matrícula con éxito.");
                        break;

                    case 6: // Cargar CSV
                        String rutaCSV = MiEntradaSalida.leerLinea("Introduce la ruta exacta del archivo CSV (ej: src/resources/BBDD Coches.csv): ");
                        baseDatos.cargarCSV(Path.of(rutaCSV));
                        System.out.println("Datos del CSV cargados y guardados correctamente.");
                        break;

                    case 7: // Ver total de registros
                        long total = baseDatos.getNumeroRegistros();
                        System.out.println("La base de datos contiene actualmente " + total + " registros.");
                        break;

                    case 0: // Salir
                        salir = true;
                        System.out.println("Saliendo del programa...");
                        break;
                }

            } catch (MiEntrdaSalidaException e) {
                System.out.println("Error de entrada de datos: " + e.getMessage());
            } catch (BBDDException e) {
                System.out.println("Error en la Base de Datos: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Se ha producido un error inesperado: " + e.getMessage());
            }
        }
    }

    private static BBDD inicializarBBDD() {
        // Configuramos el esquema exacto que pide tu constructor de BBDD
        Map<String, Integer> esquemaCampos = new HashMap<>();
        esquemaCampos.put("Matricula", 7);
        esquemaCampos.put("Marca", 32);
        esquemaCampos.put("Modelo", 32);


        try {
            PropertiesReader pr = PropertiesReader.getInstance();
            // Pasamos la ruta del archivo DAT, el mapa y el nombre del campo clave
            return new BBDD(pr.get("ruta"), esquemaCampos, "Matricula");
        } catch (AppException | IOException | BBDDException e) {
            System.out.println("Error crítico al inicializar el fichero de la Base de Datos: " + e.getMessage());
            return null;
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n--- MENÚ BÁSICO BBDD CONCESIONARIO ---");
        System.out.println("1. Insertar un nuevo registro");
        System.out.println("2. Buscar posición por matrícula");
        System.out.println("3. Borrar un registro por posición");
        System.out.println("4. Modificar un registro por posición");
        System.out.println("5. Ordenar base de datos por matrícula");
        System.out.println("6. Cargar datos masivos desde un CSV");
        System.out.println("7. Ver número total de registros");
        System.out.println("0. Salir");
    }
}