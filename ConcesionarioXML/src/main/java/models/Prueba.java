package models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.xml.bind.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Prueba {

    static void main(String[] args) {

        try {
            JAXBContext context1 = JAXBContext.newInstance(Concesionario.class);
            Unmarshaller un = context1.createUnmarshaller();
            File f2 = new File("C:\\Users\\Alvaro\\Desktop\\Ficheros\\ConcesionarioXML\\src\\main\\resources\\concesionario_plantilla.xml");
            Concesionario cXML = (Concesionario) un.unmarshal(f2);

            System.out.println(cXML);

            JAXBContext context = JAXBContext.newInstance(Concesionario.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);

            Concesionario c = new Concesionario();

            Coche c1 = new Coche("1234abc", "ford", "focus" );

            Equipamiento e = new Equipamiento("Frenos");
            Equipamiento e2 = new Equipamiento("Jose");

            List<Equipamiento> lol = new ArrayList<>();

            lol.add(e);
            lol.add(e2);

            c1.setEquipamiento(lol);

            List<Coche> coches = new ArrayList<>();

            coches.add(c1);

            c.setCoches(coches);

            File f = new File("lol.xml");

            m.marshal(c,f);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            String con = gson.toJson(c);

            Path p = Path.of("l.json");

            Files.writeString(p,con, StandardCharsets.UTF_8, StandardOpenOption.CREATE);

        } catch (JAXBException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
