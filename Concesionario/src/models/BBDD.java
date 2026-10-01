package models;

import exception.AppException;
import exception.BBDDException;
import io.MiEntradaSalida;
import io.PropertiesReader;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

    private int longitudRegistro;
    private long numeroRegistros;
    private long registrosBorrados;

    /*
    Constructor
     */
    public BBDD(String ruta, Map<String, Integer> esquemaCampos, String campoClave) throws AppException, IOException {
        contenidoFichero = new LinkedList<>();
        Path ruta1 = Path.of(ruta);
        this.ruta = ruta1;
        this.esquemaCampos = esquemaCampos;
        this.campoClave = campoClave;
        this.numeroRegistros = 0;
        this.registrosBorrados = 0;
        this.longitudRegistro = 0;

        for (Integer i : esquemaCampos.values()) {
            longitudRegistro += i;
        }

        if (Files.exists(ruta1)) {
            numeroRegistros = ruta1.toFile().length() / longitudRegistro;
        } else {
            Files.createFile(ruta1);
        }
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

    public void insertar(int pos) throws BBDDException {

        String matricula = MiEntradaSalida.leerLinea("Introduce la matrícula: \n");
        String modelo = MiEntradaSalida.leerLinea("Introduce la modelo: \n");
        String marca = MiEntradaSalida.leerLinea("Introduce la marca: \n");

        byte[] arrayMat = rellenarConBytes(MATRICULA, matricula, CANT_BYTES_MAT);
        byte[] arrayMod = rellenarConBytes(MODELO, modelo, CANT_BYTES_MOD);
        byte[] arrayMar = rellenarConBytes(MARCA, marca, CANT_BYTES_MARC);

        //Creamos un Buffer de Bytes para juntar los 3 arrays y conventirlos en 1
        byte[] registroRes = ByteBuffer.allocate(CANT_BYTES_MAT + CANT_BYTES_MOD + CANT_BYTES_MARC)
                .put(arrayMat)
                .put(arrayMod)
                .put(arrayMar)
                .array();

        if (pos == -1) {
            contenidoFichero.add(registroRes);
        }

        escribir(contenidoFichero);
    }


    private byte[] rellenarConBytes(String nombreCampo, String valor, int longitud) {
        byte[] arrayValor = getArrayBytesString(valor);

        //Comprobamos que el numero de bytes del valor no sea mayor que el registrado
        if (arrayValor.length > longitud) {
            throw new IllegalArgumentException(String.format("El valor %s del campo %s tiene mas bytes de los permitidos %d", valor, nombreCampo, longitud));
        }

        //creamos un array para guardar el valor en bytes y rellenarlo con bytes de espacio
        byte[] resultantes = new byte[longitud];
        Arrays.fill(resultantes, BYTE_ESPACIO);
        System.arraycopy(arrayValor, 0, resultantes, 0, arrayValor.length);

        return resultantes;

    }

    private List<byte[]> actualizarVariableReg() {
        List<byte[]> conjuntoBytes = new ArrayList<>();

        try (RandomAccessFile archivo = new RandomAccessFile(ruta.toFile(), "r")) {
            while (archivo.getFilePointer() + CANT_TOT_BYTES <= archivo.length()) {
                byte[] buffer = new byte[CANT_TOT_BYTES];
                archivo.readFully(buffer);

                conjuntoBytes.add(buffer);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return conjuntoBytes;
    }

    private void escribir(List<byte[]> registros) throws BBDDException {
        for (byte[] r : registros){
            try {
                Files.write(ruta,r);
            } catch (IOException e) {
                throw new BBDDException("Looool");
            }
        }
    }
}
