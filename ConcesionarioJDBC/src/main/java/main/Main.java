package main;

import exceptions.AppException;
import exceptions.BBDDException;
import exceptions.MiEntrdaSalidaException;
import io.MiEntradaSalida;
import io.PropertiesReader;
import models.Coche;
import models.Extra;
import models.Propietario;
import models.Traspaso;
import services.BBDD;
import services.Lite;
import services.SQL;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static BBDD bbdd = null;

    public static void main(String[] args) {
        boolean salir = false;

        while (!salir) {
            mostrarMenu();
            int opcion = 0;

            opcion = MiEntradaSalida.leerEntero("Elige una opción: ");

            // Mientras bbdd sea null, solo se puede conectar (1) o salir (0)
            if (bbdd == null && opcion != 1 && opcion != 0) {
                MiEntradaSalida.imprimirMensaje("Debes conectarte primero a una base de datos (opción 1) antes de usar las demás opciones.");
                continue;
            }

            try {
                switch (opcion) {
                    case 1:
                        conectarBBDD();
                        break;
                    case 2:
                        inicializarTablas();
                        break;
                    case 3:
                        registrarPropietario();
                        break;
                    case 4:
                        insertarCoche();
                        break;
                    case 5:
                        importarCochesCSV();
                        break;
                    case 6:
                        listarCochesConcesionario();
                        break;
                    case 7:
                        listarCochesPropietarios();
                        break;
                    case 8:
                        modificarCoche();
                        break;
                    case 9:
                        borrarCoche();
                        break;
                    case 10:
                        realizarTraspaso();
                        break;
                    case 11:
                        ejecutarProcedimientoMySQL();
                        break;
                    case 12:
                        generarInformeResumen();
                        break;
                    case 0:
                        if (bbdd != null) {
                            bbdd.close();
                        }
                        MiEntradaSalida.imprimirMensaje("Saliendo del programa...");
                        salir = true;
                        break;
                    default:
                        MiEntradaSalida.imprimirMensaje("Opción no válida. Inténtalo de nuevo.");
                        break;
                }
            } catch (BBDDException | AppException | MiEntrdaSalidaException e) {
                MiEntradaSalida.imprimirMensaje("Error: " + e.getMessage());
            } catch (Exception e) {
                MiEntradaSalida.imprimirMensaje("Error inesperado: " + e.getMessage());
            }
        }
    }

    private static void mostrarMenu() {
        MiEntradaSalida.imprimirMensaje("\n--- GESTIÓN CONCESIONARIO ---");
        if (bbdd == null) {
            MiEntradaSalida.imprimirMensaje("[Estado: SIN CONEXIÓN - Elige la opción 1 para conectar]");
        } else {
            MiEntradaSalida.imprimirMensaje("[Estado: CONECTADO]");
        }
        MiEntradaSalida.imprimirMensaje("1. Conectar BBDD (SQLite / MySQL)");
        MiEntradaSalida.imprimirMensaje("2. Inicializar / Crear Tablas");
        MiEntradaSalida.imprimirMensaje("3. Registrar Propietario");
        MiEntradaSalida.imprimirMensaje("4. Insertar Coche");
        MiEntradaSalida.imprimirMensaje("5. Importar Coches (CSV)");
        MiEntradaSalida.imprimirMensaje("6. Listar Coches (Concesionario)");
        MiEntradaSalida.imprimirMensaje("7. Listar Coches (Propietarios)");
        MiEntradaSalida.imprimirMensaje("8. Modificar Coche");
        MiEntradaSalida.imprimirMensaje("9. Borrar Coche");
        MiEntradaSalida.imprimirMensaje("10. Realizar Traspaso");
        MiEntradaSalida.imprimirMensaje("11. Ejecutar Procedimiento Almacenado (MySQL)");
        MiEntradaSalida.imprimirMensaje("12. Generar Informe Resumen (.txt)");
        MiEntradaSalida.imprimirMensaje("0. Salir");
    }

    // 1. Conectar BBDD
    private static void conectarBBDD() throws MiEntrdaSalidaException, BBDDException {
        MiEntradaSalida.imprimirMensaje("1) SQLite");
        MiEntradaSalida.imprimirMensaje("2) MySQL");
        int tipo = MiEntradaSalida.leerEntero("Selecciona el tipo de BBDD: ");

        if (tipo == 1) {
            try {
                bbdd = new Lite();
                MiEntradaSalida.imprimirMensaje("Conectado a SQLite correctamente.");
            } catch (SQLException e) {
                MiEntradaSalida.imprimirMensaje("Ha habido un problema al establecer la conexión: " + e.getMessage());
            }

        } else if (tipo == 2) {
            try {
                bbdd = new SQL();
                MiEntradaSalida.imprimirMensaje("Conectado a MySQL correctamente.");
            } catch (SQLException e) {
                MiEntradaSalida.imprimirMensaje("Ha habido un problema al establecer la conexión: " + e.getMessage());
            }

        } else {
            MiEntradaSalida.imprimirMensaje("Tipo de base de datos no válido.");
        }
    }

    // 2. Inicializar/Crear Tablas
    private static void inicializarTablas() throws BBDDException {
        bbdd.crearTablas();
        MiEntradaSalida.imprimirMensaje("Tablas verificadas/creadas correctamente.");
    }

    // 3. Registrar Propietario
    private static void registrarPropietario() throws MiEntrdaSalidaException, BBDDException {
        String dni = MiEntradaSalida.leerLinea("DNI del propietario: ");
        String nombre = MiEntradaSalida.leerLinea("Nombre: ");
        String apellidos = MiEntradaSalida.leerLinea("Apellidos: ");
        String telefono = MiEntradaSalida.leerLinea("Teléfono: ");

        Propietario prop = new Propietario(dni, nombre, apellidos, telefono);
        bbdd.insertPropietario(prop);
        MiEntradaSalida.imprimirMensaje("Propietario registrado con éxito.");
    }

    // 4. Insertar Coche (sin propietario -> pertenece al concesionario)
    private static void insertarCoche() throws MiEntrdaSalidaException, BBDDException {
        String matricula = MiEntradaSalida.leerLinea("Matrícula: ");
        String marca = MiEntradaSalida.leerLinea("Marca: ");
        String modelo = MiEntradaSalida.leerLinea("Modelo: ");
        double precio = MiEntradaSalida.leerDouble("Precio: ");
        List<Extra> extras = leerEquipamiento();

        Coche coche = new Coche(matricula, marca, modelo, extras, precio);
        bbdd.insertarCoche(coche);
        MiEntradaSalida.imprimirMensaje("Coche añadido al concesionario.");
    }

    private static List<Extra> leerEquipamiento() {
        List<Extra> equipamiento = new ArrayList<>();
        while (confirmarAnadirExtra()) {
            String nombre = MiEntradaSalida.leerLinea("Nombre del extra: ").trim();
            if (!nombre.isEmpty()) {
                equipamiento.add(new Extra(nombre));
            } else {
                System.out.println("El nombre del extra no puede estar vacío.");
            }
        }
        return equipamiento;
    }

    private static boolean confirmarAnadirExtra() {
        while (true) {
            String respuesta = MiEntradaSalida.leerLinea("¿Añadir un extra? (S/N): ").trim();
            if (respuesta.equalsIgnoreCase("S")) {
                return true;
            }
            if (respuesta.equalsIgnoreCase("N")) {
                return false;
            }
            System.out.println("Respuesta no válida. Escribe S o N.");
        }
    }

    // 5. Importar Coches (CSV) de forma transaccional
    private static void importarCochesCSV() throws MiEntrdaSalidaException, BBDDException, AppException {

        PropertiesReader p = PropertiesReader.getInstance();

        Path rutaDefault = Path.of(p.get("csv.ruta"));
        if (MiEntradaSalida.leerSN("¿Quieres usar la ruta predeterminada(Introduce s o n)?\n") == 'S'){
            bbdd.insertarDesdeCSV(rutaDefault);
            MiEntradaSalida.imprimirMensaje("Importación completada");
        }
        String rutaCSV = MiEntradaSalida.leerLinea("Ruta del fichero CSV: ");
        bbdd.insertarDesdeCSV(Path.of(rutaCSV));
        MiEntradaSalida.imprimirMensaje("Importación completada");
    }

    // 6. Listar Coches (Concesionario - sin propietario)
    private static void listarCochesConcesionario() throws BBDDException {
        MiEntradaSalida.imprimirMensaje("\n--- COCHES EN CONCESIONARIO ---");
        for (Coche c : bbdd.mostrarCochesSinPropietarios()) {
            MiEntradaSalida.imprimirMensaje(c.toString());
        }
    }

    // 7. Listar Coches (Propietarios - con DNI mediante JOIN)
    private static void listarCochesPropietarios() throws BBDDException {
        MiEntradaSalida.imprimirMensaje("\n--- COCHES CON PROPIETARIO ---");
        for (Coche c : bbdd.mostrarCochesConPropietarios()) {
            MiEntradaSalida.imprimirMensaje(c.toString());
        }
    }

    // 8. Modificar Coche (excepto matrícula)
    private static void modificarCoche() throws MiEntrdaSalidaException, BBDDException {
        String matricula = MiEntradaSalida.leerLinea("Matrícula del coche a modificar: ");
        String marca = MiEntradaSalida.leerLinea("Marca: ");
        String modelo = MiEntradaSalida.leerLinea("Modelo: ");
        double precio = MiEntradaSalida.leerDouble("Precio: ");
        List<Extra> extras = leerEquipamiento();

        bbdd.modificarCoche(new Coche(matricula,marca,modelo,extras,precio));
        MiEntradaSalida.imprimirMensaje("Coche modificado correctamente.");
    }

    // 9. Borrar Coche
    private static void borrarCoche() throws MiEntrdaSalidaException, BBDDException {
        String matricula = MiEntradaSalida.leerLinea("Matrícula del coche a borrar: ");
        bbdd.borrarCoche(matricula);
        MiEntradaSalida.imprimirMensaje("Coche eliminado correctamente.");
    }

    // 10. Realizar Traspaso (transaccional + verifica comprador y coche)
    private static void realizarTraspaso() throws MiEntrdaSalidaException, BBDDException {
        int idvendedor = MiEntradaSalida.leerEntero("Id del vendedor (si es el concesionario pon 0 y si pones uno que no existe se tomara que tambien es el concesionario): ");
        int idPropietario = MiEntradaSalida.leerEntero("Id del nuevo propietario: ");
        String matricula = MiEntradaSalida.leerLinea("Matrícula del coche: ");
        double monto = MiEntradaSalida.leerDouble("Monto económico: ");


        bbdd.traspasarCoche(matricula,idPropietario,idvendedor,monto);
        MiEntradaSalida.imprimirMensaje("Traspaso realizado con éxito.");
    }

    // 11. Ejecutar Procedimiento Almacenado (Solo MySQL)
    private static void ejecutarProcedimientoMySQL() throws MiEntrdaSalidaException, BBDDException {
        if (!(bbdd instanceof SQL)) {
            MiEntradaSalida.imprimirMensaje("Esta opción solo está disponible para MySQL.");
            return;
        }
        String marca = MiEntradaSalida.leerLinea("Marca a consultar (sp_coches_por_marca): ");
        MiEntradaSalida.imprimirMensaje("\n--- RESULTADO PROCEDIMIENTO ---");
        for (Coche c : ((SQL) bbdd).ejecutarProcedimiento(marca)) {
            MiEntradaSalida.imprimirMensaje(c.toString());
        }
    }

    // 12. Generar Informe Resumen (informe_concesionario.txt)
    private static void generarInformeResumen() throws BBDDException {
        bbdd.generarInformeResumen();
        MiEntradaSalida.imprimirMensaje("Informe generado correctamente");
    }
}