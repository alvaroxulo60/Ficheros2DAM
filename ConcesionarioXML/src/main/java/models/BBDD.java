package models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.nio.file.Path;

public class BBDD {

    private Concesionario c;
    private Path ruta;
    private JAXBContext context;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private Gson gson;

    public BBDD(Path ruta) throws JAXBException {
        this.ruta = ruta;
        context = JAXBContext.newInstance(Concesionario.class);
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
        gson = new GsonBuilder().setPrettyPrinting().create();
    }


    private void actuConcesionario(){
        c = unmarshaller.unmarshal(this.ruta);
    }
}
