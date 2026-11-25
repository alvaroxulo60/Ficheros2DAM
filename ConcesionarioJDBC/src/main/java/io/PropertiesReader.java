package io;

import exceptions.AppException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private static final String FILE_NAME = "/config.properties";
    private Properties p;
    private static PropertiesReader instance = null;

    /** Carga las opciones guardadas en config.properties. */
    private PropertiesReader() throws AppException {
        p = new Properties();
        try (InputStream lector = PropertiesReader.class.getResourceAsStream(FILE_NAME)) {
            if (lector == null) {
                throw new AppException("El archivo config.properties no se encuentra en src/main/resources.");
            }
            p.load(lector);
        } catch (IOException e) {
            throw new AppException(e.getMessage());
        }
    }

    /** Devuelve el lector compartido de configuración. */
    public static PropertiesReader getInstance() throws AppException {
        if (instance == null){
            instance = new PropertiesReader();
        }

        return instance;
    }

    /** Devuelve el valor de la opción indicada. */
    public String get(String propiedad){
        return p.getProperty(propiedad);
    }
}
