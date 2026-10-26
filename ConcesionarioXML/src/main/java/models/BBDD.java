package models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.BBDDException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class BBDD {

    private Concesionario c;
    private Path ruta;
    private JAXBContext context;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private Gson gson;

    public BBDD(Path ruta) throws JAXBException, BBDDException {
        this.ruta = ruta;
        context = JAXBContext.newInstance(Concesionario.class);
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
        unmarshaller = context.createUnmarshaller();
        gson = new GsonBuilder().setPrettyPrinting().create();
        actuConcesionario();
    }

    /**
     * Metodo para actualizar la variable concesionario y tener toda la información del csv
     *
     * @throws BBDDException
     */
    private void actuConcesionario() throws BBDDException {
        try {
            c = (Concesionario) unmarshaller.unmarshal(this.ruta.toFile());
        } catch (JAXBException e) {
            throw new BBDDException("Fallo al leer el archivo XML");
        }
    }

    /**
     * Metoodo para escribir toda la informacion que este en el concesionario al csv
     *
     * @throws BBDDException
     */
    private void escribir() throws BBDDException {
        try {
            marshaller.marshal(c, this.ruta.toFile());
            actuConcesionario();
        } catch (JAXBException e) {
            throw new BBDDException("No se ha podido escribir el fichero XML");
        }
    }

    /**
     * Metodo para insertar un coche teniendo en cuenta que no este repetida la matricula
     *
     * @param coche coche a insetar
     * @throws BBDDException matricula repetida
     */
    public void insertar(Coche coche) throws BBDDException {
        if (comprobarRepetidos(coche) == -1) {
            throw new BBDDException("Matricula repetida, no se ha podido insertar");
        }
        c.getCoches().add(coche);
        escribir();
    }

    /**
     * Metodo para comprobar que la matricula del nuevo coche no este repetido
     *
     * @param coche coche a comprobar
     * @return
     */
    private int comprobarRepetidos(Coche coche) {
        List<Coche> coches = c.getCoches();

        for (Coche c : coches) {
            if (c.getMatricula().equals(coche.getMatricula())) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * Metodo para insertar datos dese un csv
     *
     * @param ruta ruta al csv
     * @throws BBDDException
     */
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

                String[] equipamientosString = extras.split(separadorExtra);

                List<Equipamiento> equipamientos = new ArrayList<>();

                for (String s : equipamientosString) {
                    equipamientos.add(new Equipamiento(s));
                }

                Coche c1 = new Coche(matricula, marca, modelo);
                c1.setEquipamiento(equipamientos);

                coches.add(c1);

            }

            for (Coche c2 : coches) {
                insertar(c2);
            }
        } catch (IOException e) {
            throw new BBDDException("No se ha podido encontrar el archivo csv");
        }
    }

    /**
     * Metodo para ordenar la lista de coches por matricula
     *
     * @throws BBDDException
     */
    public void ordenarPorMatricula() throws BBDDException {
        c.getCoches().sort(Comparator.comparing(Coche::getMatricula));
        escribir();
    }

    /**
     *
     * Metodo para buscarun coche por matricula
     *
     * @param matricula matricula a buscar
     * @return El coche si encuentra la matricula o null si no
     */
    private Coche buscarMatricula(String matricula) {
        List<Coche> coches = c.getCoches();

        for (Coche coche : coches) {
            if (coche.getMatricula().equals(matricula)) {
                return coche;
            }
        }
        return null;
    }

    /**
     * Metodo para borrar un coche a base de una matricula
     *
     * @param matricula matricula a borrar
     * @throws BBDDException
     */
    public void borrar(String matricula) throws BBDDException {
        Coche coche = buscarMatricula(matricula);

        if (coche != null) {
            c.getCoches().remove(coche);
            escribir();
        } else {
            throw new BBDDException("Matricula no encontrada");
        }
    }

    /**
     * Metodo para modificar un coche
     *
     * @param matricula matricula del coche
     * @param marca     nueva marca
     * @param modelo    nuevo modelo
     * @param extras    lista de equipamientos del coche
     * @throws BBDDException
     */
    public void modificarCoche(String matricula, String marca, String modelo, List<Equipamiento> extras) throws BBDDException {
        Coche coche = buscarMatricula(matricula);

        if (coche != null) {
            coche.setModelo(modelo);
            coche.setMarca(marca);
            coche.setEquipamiento(extras);
            escribir();
        } else {
            throw new BBDDException("Matricula no encontrada");
        }
    }

    /**
     * Metodo para exportar la bbdd a JSON
     *
     * @param ruta ruta al fichero
     * @throws BBDDException
     */
    public void exportarBBDDJSON(Path ruta) throws BBDDException {
        String json = gson.toJson(c);

        try {
            Files.writeString(ruta, json, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new BBDDException("No se ha podido exportar la BBDD");
        }
    }

    /**
     * Metodo para exportar un coche a JSON
     *
     * @param matricula matricula del coche
     * @param ruta      ruta del fichero
     * @throws BBDDException
     */
    public void exportarCocheJSON(String matricula, Path ruta) throws BBDDException {
        Coche coche = buscarMatricula(matricula);

        if (coche != null) {
            String json = gson.toJson(coche);
            try {
                Files.writeString(ruta, json, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            } catch (IOException e) {
                throw new BBDDException("No se ha podido exportar el coche");
            }
        } else {
            throw new BBDDException("No se ha encontrado el coche");
        }
    }

    /**
     * Metodo para importar una bbdd desd json sobrescribiendo la que existe
     *
     * @param ruta
     * @throws BBDDException
     */
    public void importarDesdeJSON(Path ruta) throws BBDDException {
        try (Reader r = new FileReader(ruta.toFile())) {
            c = gson.fromJson(r, Concesionario.class);
            escribir();

        } catch (IOException e) {
            throw new BBDDException("No se ha podido importar");
        }
    }


    /**
     * Metodo para importar un coche desde json
     *
     * @param ruta ruta al fichero
     * @throws BBDDException
     */
    public void importarCocheDesdeJSON(Path ruta) throws BBDDException {
        try (Reader r = new FileReader(ruta.toFile())) {
            Coche coche = gson.fromJson(r, Coche.class);
            insertar(coche);
        } catch (IOException e) {
            throw new BBDDException("No se ha podido importar coche");


        }
    }

    public void crearResumen() throws BBDDException {

        Path p = Path.of("informe_concesionario.txt");

        int numeroDeCoches = c.getCoches().size();

        Map<String, List<Coche>> grupoMarca = new HashMap<>();

        for (Coche coche : c.getCoches()) {
            String marca = coche.getMarca();
            if (!grupoMarca.containsKey(marca)) {
                grupoMarca.put(marca, new ArrayList<>());
            }

            grupoMarca.get(marca).add(coche);
        }

        Map<String, Integer> numEquipamiento = new HashMap<>();

        for (Coche co : c.getCoches()) {
            for (Equipamiento e : co.getEquipamiento()) {
                String nombre = e.getNombre();
                if (!numEquipamiento.containsKey(nombre)) {
                    numEquipamiento.put(nombre, 1);
                } else {
                    numEquipamiento.compute(nombre, (k, v) -> (v == null) ? 1 : v+1);
                }

            }
        }

        StringBuilder informe = new StringBuilder();

        informe.append("=== INFORME DEL CONCESIONARIO ===\n");
        informe.append("Número total de coches: ").append(numeroDeCoches).append("\n\n");

        informe.append("--- COCHES POR MARCA ---\n");
        for (Map.Entry<String, List<Coche>> entry : grupoMarca.entrySet()) {
            informe.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue().size())
                    .append(" coches\n");
        }

        informe.append("\n--- EQUIPAMIENTO MAS REPETIDO ---\n");
        String equipamientoTop = "Ninguno";
        int maxRepeticiones = 0;

        // Recorremos el mapa para encontrar el que tiene el número mayor
        for (Map.Entry<String, Integer> entry : numEquipamiento.entrySet()) {
            if (entry.getValue() > maxRepeticiones) {
                maxRepeticiones = entry.getValue();
                equipamientoTop = entry.getKey();
            }
        }

        // Lo añadimos al informe si encontramos al menos uno
        if (maxRepeticiones > 0) {
            informe.append(equipamientoTop)
                    .append(": ")
                    .append(maxRepeticiones)
                    .append(" unidades\n");
        } else {
            informe.append("No hay equipamiento registrado.\n");
        }

        try {
            Files.writeString(p,informe.toString(),StandardCharsets.UTF_8,StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new BBDDException("No se ha podido hacer el informe");
        }
    }

}
