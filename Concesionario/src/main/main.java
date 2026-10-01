package main;

import exception.AppException;
import exception.BBDDException;
import io.PropertiesReader;
import models.BBDD;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class main {
    static void main(String[] args) {
        PropertiesReader p;
        try {
            Map<String,Integer> esquema = new HashMap<>();
            esquema.put("Matricula",7);
            esquema.put("Marca",32);
            esquema.put("modelo",32);
            p = PropertiesReader.getInstance();
            BBDD  b = new BBDD(p.get("ruta"),esquema,"Matricula");
            b.insertar(-1);
        } catch (AppException e) {
            System.out.println("No");
        } catch (IOException | BBDDException e) {
            System.out.println(e.getMessage());
        }


    }
}
