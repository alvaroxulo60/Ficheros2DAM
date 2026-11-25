package models;

import java.util.List;

public class Coche {

    private String matricula;
    private String marca;
    private String modelo;
    private List<Extra> extras;
    private double precio;
    private int idPropietario;

    /** Crea un coche con sus datos y el identificador de su propietario. */
    public Coche(String matricula, String marca, String modelo, List<Extra> extras, double precio, int idPropietario) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.extras = extras;
        this.precio = precio;
        this.idPropietario = idPropietario;
    }

    /** Crea un coche que todavía no tiene propietario asignado. */
    public Coche(String matricula, String marca, String modelo, List<Extra> extras, double precio) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.extras = extras;
        this.precio = precio;
    }

    /** Devuelve la matrícula del coche. */
    public String getMatricula() {
        return matricula;
    }

    /** Devuelve la marca del coche. */
    public String getMarca() {
        return marca;
    }

    /** Devuelve el modelo del coche. */
    public String getModelo() {
        return modelo;
    }

    /** Devuelve los extras del coche. */
    public List<Extra> getExtras() {
        return extras;
    }

    /** Devuelve el precio del coche. */
    public double getPrecio() {
        return precio;
    }

    /** Devuelve el identificador del propietario. */
    public int getIdPropietario() {
        return idPropietario;
    }

    /** Devuelve los datos del coche en forma de texto. */
    @Override
    public String toString() {
        return "Coche{" +
                "matricula='" + matricula + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", extras=" + extras +
                ", precio=" + precio +
                ", idPropietario=" + idPropietario +
                '}';
    }
}
