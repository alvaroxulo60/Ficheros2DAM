package models;

import exception.AppException;
import exception.BBDDException;
import io.MiEntradaSalida;
import io.PropertiesReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class BBDD {
    private static final byte BYTE_ESPACIO = (byte) ' ';
    private static final int CANT_BYTES_MAT = 7;
    private static final String MATRICULA = "Matricula";
    private static final int CANT_BYTES_MARC = 32;
    private static final String MARCA = "Marca";
    private static final int CANT_BYTES_MOD = 32;
    private static final String MODELO = "Modelo";
    private static final int CANT_TOT_BYTES = 51;

    private List<byte[]> contenidoFichero;


    private final Path ruta;
    private final Map<String, Integer> esquemaCampos;
    private final String campoClave;

    private long numeroRegistros;


    /*
    Constructor
     */
    public BBDD(String ruta, Map<String, Integer> esquemaCampos, String campoClave) throws AppException, IOException {
        contenidoFichero = new LinkedList<>();
        this.ruta = Path.of(ruta);
        this.esquemaCampos = esquemaCampos;
        this.campoClave = campoClave;
        this.numeroRegistros = 0;

        if (!Files.exists(this.ruta)) {
            Files.createFile(this.ruta);
        }
        actualizarVariableReg();
    }

    /**
     * Devolver el array de bytes de un texto
     *
     * @param texto a volver a bytes
     * @return el array de bytes
     */
    private byte[] getArrayBytesString(String texto) {
        return texto.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Metodo para insertar un registro en la bbdd
     *
     * @param pos posición en la que insertar el registro (-1 si es en la última posición)
     * @throws BBDDException
     */
    public void insertar(int pos) throws BBDDException {
        //Pedimos los datos del registro

        String matricula = MiEntradaSalida.leerLinea("Introduce la matrícula: \n");
        String marca = MiEntradaSalida.leerLinea("Introduce la marca: \n");
        String modelo = MiEntradaSalida.leerLinea("Introduce la modelo: \n");


        // Si ya existe esa matricula lanzamos una excepción
        if (buscar(matricula) == -1) {
            throw new BBDDException("La Primary key (matrícula) esta repetida");
        }


        //Llama al metodo para crear el array de bytes del registro
        byte[] registroRes = crearArrayDeBytesDeRegistro(matricula, modelo, marca);

        if (pos == -1 || pos > this.numeroRegistros) {
            contenidoFichero.add(registroRes);
        } else {
            contenidoFichero.add(pos, registroRes);
        }

        escribir();
    }

    /**
     * Rellena un valor con bytes de espacio para cumplir la estructura de la bbdd
     *
     * @param nombreCampo el nombe del campo del valor
     * @param valor       el valor a convertir en bytes
     * @param longitud    la longitud en bytes de ese valor que debe ocupar en la bbdd
     * @return el array nuevo cumpliendo la estructura de bbdd
     */
    private byte[] rellenarConBytes(String nombreCampo, String valor, int longitud) {
        byte[] arrayValor = getArrayBytesString(valor);

        //Comprueba que el numero de bytes del valor no sea mayor que el registrado
        if (arrayValor.length > longitud) {
            throw new IllegalArgumentException(String.format("El valor %s del campo %s tiene mas bytes de los permitidos %d", valor, nombreCampo, longitud));
        }

        //crea un array para guardar el valor en bytes y rellenarlo con bytes de espacio
        byte[] resultantes = new byte[longitud];
        Arrays.fill(resultantes, BYTE_ESPACIO);
        System.arraycopy(arrayValor, 0, resultantes, 0, arrayValor.length);

        return resultantes;

    }

    /**
     * Crea el array de bytes de un registro
     *
     * @param mat la matricula
     * @param mod el modelo
     * @param mar la marca
     * @return el array resultante de la mezcla de los 3
     */
    private byte[] crearArrayDeBytesDeRegistro(String mat, String mar, String mod) {
        //Rellenamos cada valor con bytes para cumplir los estandares de la bbdd
        byte[] arrayMat = rellenarConBytes(MATRICULA, mat, CANT_BYTES_MAT);
        byte[] arrayMod = rellenarConBytes(MODELO, mod, CANT_BYTES_MOD);
        byte[] arrayMar = rellenarConBytes(MARCA, mar, CANT_BYTES_MARC);

        //Creamos un Buffer de Bytes para juntar los 3 arrays y conventirlos en 1
        byte[] registroRes = ByteBuffer.allocate(CANT_BYTES_MAT + CANT_BYTES_MOD + CANT_BYTES_MARC)
                .put(arrayMat)
                .put(arrayMar)
                .put(arrayMod)
                .array();

        return registroRes;
    }

    /**
     * Lee todo el contenido del fichero y lo volcamos en la variable para hacer toda la gestion logica a partir de esa variable
     *
     * @return
     */
    private void actualizarVariableReg() {
        List<byte[]> conjuntoBytes = new ArrayList<>();

        try (RandomAccessFile archivo = new RandomAccessFile(ruta.toFile(), "r")) {
            //Va leyendo mientras que la posición del puntero mas la cantidad de bytes de un registro no sea mayor que la longitud del archivo
            while (archivo.getFilePointer() + CANT_TOT_BYTES <= archivo.length()) {
                byte[] buffer = new byte[CANT_TOT_BYTES];
                //Lee el registro entero con el buffer y lo añade a la lista
                archivo.readFully(buffer);

                conjuntoBytes.add(buffer);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        this.contenidoFichero = conjuntoBytes;
        this.numeroRegistros = contenidoFichero.size();
    }

    /**
     * Coge todos los registros de la variable y los vuelca al fichero
     *
     * @throws BBDDException
     */
    private void escribir() throws BBDDException {
        for (byte[] r : contenidoFichero) {
            try {
                Files.write(ruta, r);
            } catch (IOException e) {
                throw new BBDDException("Looool");
            }
        }
        //Actualizamos el valor de la variable siempre despues de escribir
        actualizarVariableReg();
    }

    /**
     * Ordenar la coleccion por la matricula
     *
     * @throws BBDDException
     */

    public void ordenarPorMatricula() throws BBDDException {
        //Ordenamos la coleccion utilizando el metodo compare de Arrays donde comparamos los 7 primeros bytes de cada registro
        contenidoFichero.sort((b1, b2) -> {
            return Arrays.compare(b1, 0, 7, b2, 0, 7);
        });
        escribir();
    }

    /**
     * Buscar un registro por una matrícula en la base de datos
     *
     * @param mat matricula a buscar
     * @return la posición en la bbdd de ese registro y -1 si no lo encuentra
     * @throws BBDDException
     */

    public int buscar(String mat) throws BBDDException {
        byte[] bytesMat = getArrayBytesString(mat);
        if (bytesMat.length > CANT_BYTES_MAT) {
            throw new BBDDException("La matrícula introducida no es válida");
        }
        //Recorremos el contenido del fichero y por cada uno cogemos el array de bytes
        for (int i = 0; i < contenidoFichero.size(); i++) {
            byte[] elemento = contenidoFichero.get(i);

            //Comparamos los 7 primeros bytes del elemento y los bytes de la matricula y si son iguales (==0) devolvemos la posicion
            if (Arrays.compare(elemento, 0, 7, bytesMat, 0, 7) == 0) {
                return i;
            }
        }
        //Si no devolvemos -1
        return -1;
    }

    /**
     * Borra un registro en x posición
     *
     * @param pos la posición del registro
     * @throws BBDDException
     */
    public void borrar(int pos) throws BBDDException {
        if (pos > numeroRegistros) {
            throw new BBDDException("No existe un registro en esa posición");
        }
        contenidoFichero.remove(pos);
        escribir();
    }

    /**
     * Cargar el contenido de un fichero csv a la bbdd
     *
     * @param ruta la ruta del fichero csv
     * @throws BBDDException
     */
    public void cargarCSV(Path ruta) throws BBDDException {
        String separador = ",";
        try (Stream<String> lineas = Files.lines(ruta)) {

            lineas.skip(1)
                    .forEach(l -> {
                        String[] valores = l.split(separador);

                        String mat = valores[0];
                        String mar = valores[1];
                        String mod = valores[2];

                        byte[] reg = crearArrayDeBytesDeRegistro(mat, mar, mod);

                        contenidoFichero.add(reg);
                    });
            escribir();

        } catch (IOException e) {
            throw new BBDDException("Error: " + e.getMessage());
        }
    }

    public long getNumeroRegistros() {
        return numeroRegistros;
    }
}
