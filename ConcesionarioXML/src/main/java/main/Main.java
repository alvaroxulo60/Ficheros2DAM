package main;

import exception.AppException;
import exception.BBDDException;
import exception.MiEntrdaSalidaException;
import io.MiEntradaSalida;
import io.PropertiesReader;
import jakarta.xml.bind.JAXBException;
import models.BBDD;
import models.Coche;
import models.Equipamiento;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            PropertiesReader propiedades = PropertiesReader.getInstance();
            Path rutaBBDD = Path.of(propiedades.get("bbdd"));
            inicializarBBDD(rutaBBDD);

            BBDD bbdd = new BBDD(rutaBBDD);
            ejecutarMenu(bbdd, propiedades);
        } catch (AppException | BBDDException | JAXBException | MiEntrdaSalidaException e) {
            System.out.println("No se pudo iniciar la aplicación: " + e.getMessage());
        }
    }

    private static void ejecutarMenu(BBDD bbdd, PropertiesReader propiedades) throws MiEntrdaSalidaException {
        int opcion;
        do {
            System.out.println("\n=== CONCESIONARIO ===");
            System.out.println("1. Insertar coche");
            System.out.println("2. Importar coches desde CSV");
            System.out.println("3. Ordenar coches por matrícula");
            System.out.println("4. Borrar coche");
            System.out.println("5. Modificar coche");
            System.out.println("6. Exportar concesionario a JSON");
            System.out.println("7. Exportar coche a JSON");
            System.out.println("8. Importar concesionario desde JSON");
            System.out.println("9. Importar coche desde JSON");
            System.out.println("10. Crear resumen");
            System.out.println("0. Salir");

            opcion = MiEntradaSalida.leerEnteroRango("Elige una opción: ", 0, 10);
            try {
                switch (opcion) {
                    case 1 -> insertarCoche(bbdd);
                    case 2 -> bbdd.insertarDesdeCSV(leerRuta("Ruta del CSV (vacío para usar la configurada): ", propiedades.get("csv")));
                    case 3 -> bbdd.ordenarPorMatricula();
                    case 4 -> bbdd.borrar(MiEntradaSalida.leerLinea("Matrícula del coche que quieres borrar: "));
                    case 5 -> modificarCoche(bbdd);
                    case 6 -> bbdd.exportarBBDDJSON(leerRuta(
                            "Ruta del JSON de salida (vacío para usar la configurada): ",
                            propiedades.get("jsonExportacionBBDD")));
                    case 7 -> bbdd.exportarCocheJSON(
                            MiEntradaSalida.leerLinea("Matrícula del coche: "),
                            leerRuta("Ruta del JSON de salida (vacío para usar la configurada): ",
                                    propiedades.get("jsonExportacionCoche")));
                    case 8 -> bbdd.importarDesdeJSON(leerRuta(
                            "Ruta del JSON de entrada (vacío para usar la configurada): ",
                            propiedades.get("jsonConcesionario")));
                    case 9 -> bbdd.importarCocheDesdeJSON(leerRuta(
                            "Ruta del JSON de entrada (vacío para usar la configurada): ",
                            propiedades.get("jsonCoche")));
                    case 10 -> bbdd.crearResumen();
                    case 0 -> System.out.println("Hasta luego.");
                }
                if (opcion != 0) {
                    System.out.println("Operación completada.");
                }
            } catch (BBDDException e) {
                System.out.println("No se pudo completar la operación: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void insertarCoche(BBDD bbdd) throws BBDDException {
        String matricula = MiEntradaSalida.leerLinea("Matrícula: ");
        String marca = MiEntradaSalida.leerLinea("Marca: ");
        String modelo = MiEntradaSalida.leerLinea("Modelo: ");
        List<Equipamiento> equipamiento = leerEquipamiento();
        Coche coche = new Coche(matricula, marca, modelo);
        coche.setEquipamiento(equipamiento);
        bbdd.insertar(coche);
    }

    private static void modificarCoche(BBDD bbdd) throws BBDDException {
        String matricula = MiEntradaSalida.leerLinea("Matrícula del coche que quieres modificar: ");
        String marca = MiEntradaSalida.leerLinea("Nueva marca: ");
        String modelo = MiEntradaSalida.leerLinea("Nuevo modelo: ");
        bbdd.modificarCoche(matricula, marca, modelo, leerEquipamiento());
    }

    private static List<Equipamiento> leerEquipamiento() {
        List<Equipamiento> equipamiento = new ArrayList<>();
        while (confirmarAnadirExtra()) {
            String nombre = MiEntradaSalida.leerLinea("Nombre del extra: ").trim();
            if (!nombre.isEmpty()) {
                equipamiento.add(new Equipamiento(nombre));
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

    private static Path leerRuta(String mensaje, String rutaPorDefecto) {
        String ruta = MiEntradaSalida.leerLinea(mensaje).trim();
        if (ruta.isEmpty() && rutaPorDefecto != null) {
            ruta = rutaPorDefecto;
        }
        return Path.of(ruta);
    }

    private static void inicializarBBDD(Path rutaBBDD) throws AppException {
        if (Files.exists(rutaBBDD)) {
            return;
        }
        try {
            Path directorio = rutaBBDD.toAbsolutePath().getParent();
            if (directorio != null) {
                Files.createDirectories(directorio);
            }
            try (InputStream plantilla = Main.class.getResourceAsStream("/concesionario_plantilla.xml")) {
                if (plantilla == null) {
                    throw new IOException("No se encontró concesionario_plantilla.xml en los recursos.");
                }
                Files.copy(plantilla, rutaBBDD);
            }
        } catch (IOException e) {
            throw new AppException("No se pudo crear el archivo de datos: " + e.getMessage());
        }
    }
}
