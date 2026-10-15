package models;

import io.LocalDateAdapter;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDate;

@XmlRootElement(name = "videojuego")
@XmlType(propOrder = {"titulo", "genero", "precio", "fechaLanzamiento"})
public class Videojuego {
    private int id;
    private String titulo;
    private String genero;
    private double precio;
    private LocalDate fechaLanzamiento;

    public Videojuego() {
    } // Requerido por JAXB

    public Videojuego(int id, String titulo, String genero, double precio, LocalDate
            fechaLanzamiento) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.precio = precio;
        this.fechaLanzamiento = fechaLanzamiento;
    }

    @XmlAttribute
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @XmlElement
    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    @XmlElement(name = "precioEuros")
    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    @XmlElement
    public LocalDate getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(LocalDate fechaLanzamiento) {
        this.fechaLanzamiento
                = fechaLanzamiento;
    }
}
