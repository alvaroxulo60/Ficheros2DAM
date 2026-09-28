package models;

import exception.AppException;
import io.PropertiesReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

public class DDBB {
    private static final byte BYTE_ESPACIO = (byte) ' ';
    private static PropertiesReader pr;

    private final Path ruta;
    private final Map<String, Integer> esquemaCampos;
    private final String campoClave;

    private int longitudRegistro;
    private long numeroRegistros;
    private long registrosBorrados;

    /*
    Constructor
     */
    public DDBB(String ruta, Map<String, Integer> esquemaCampos, String campoClave) throws AppException, IOException {
        pr = PropertiesReader.getInstance();
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

    public int insertar(Map<String, String> valores) {

    }


    private byte[] rellenarConBytes(String nombreCampo, String valor, int longitud) {
        byte[] arrayValor = getArrayBytesString(valor);

        if (arrayValor.length > longitud){
            throw new IllegalArgumentException(String.format("El valor %s del campo %s tiene mas bytes de los permitidos %d",valor,nombreCampo,longitud))
        }

        byte[] resultantes = new byte[longitud];
        Arrays.fill(resultantes,BYTE_ESPACIO);
        System.arraycopy(arrayValor,0,resultantes,0, resultantes.length);

        return resultantes;

    }
}
