package models;


import jakarta.xml.bind.*;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class MainApp {
    private static final String ARCHIVO_XML = "catalogo_tienda.xml";

    public static void main(String[] args) {
        System.out.println("=== 1. MARSHALLING (Java -> XML) ===");
        exportarXML();
        System.out.println("\n=== 2. UNMARSHALLING (XML -> Java) ===");
        importarYModificarXML();
    }

    private static void exportarXML() {
        try {
            CatalogoTienda tienda = new CatalogoTienda("GAME DAM Central",
                    "ADMIN_SECRET_123");
            tienda.getListaJuegos().add(new Videojuego(101, "The Legend of Zelda: Tears of the Kingdom", "Aventura", 69.99, LocalDate.of(2023, 5, 12)));
            tienda.getListaJuegos().add(new Videojuego(102, "Cyberpunk 2077", "RPG", 29.99, LocalDate.of(2020, 12, 10)));
            tienda.getListaJuegos().add(new Videojuego(103, "Hollow Knight", "Metroidvania", 14.99, LocalDate.of(2017, 2, 24)));
            JAXBContext context = JAXBContext.newInstance(CatalogoTienda.class);
            Marshaller marshaller = context.createMarshaller();
            // Formateado con sangría
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File destino = new File(ARCHIVO_XML);
            marshaller.marshal(tienda, destino);
            System.out.println("¡XML generado con éxito en: " + destino.getAbsolutePath());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private static void importarYModificarXML() {
        try {
            File archivo = new File(ARCHIVO_XML);
            if (!archivo.exists()) {
                System.err.println("El archivo XML no existe.");
                return;
            }
            JAXBContext context = JAXBContext.newInstance(CatalogoTienda.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            // Deserialización
            CatalogoTienda tienda = (CatalogoTienda) unmarshaller.unmarshal(archivo);
            System.out.println("Tienda leída: " + tienda.getNombreTienda());
            System.out.println("Clave Admin (debe ser null): " + tienda.getClaveAdmin());
            System.out.println("Listado de Juegos cargados:");
            for (Videojuego v : tienda.getListaJuegos()) {
                System.out.printf(" - [%d] %s (%s) - %.2f € - Fecha: %s\n",
                        v.getId(), v.getTitulo(), v.getGenero(), v.getPrecio(),
                        v.getFechaLanzamiento());
                //Serializamos en un nuevo archivo los juegos de mas de 30€ con descuento
                if (v.getPrecio() > 30){
                    File f = new File("catalogo_tienda_rebajado.xml");
                    Marshaller m = context.createMarshaller();
                    CatalogoTienda tiendaRebajada = new CatalogoTienda("GAME DAM Central","Admin123");

                    BigDecimal b = new BigDecimal(v.getPrecio() - (v.getPrecio() * 0.15));
                    b = b.setScale(2, RoundingMode.HALF_UP);
                    double precio = b.doubleValue();

                    v.setPrecio(precio);
                    tiendaRebajada.getListaJuegos().add(v);
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
                    m.marshal(tiendaRebajada,f);
                }
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}