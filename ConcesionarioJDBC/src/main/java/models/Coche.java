package models;

import java.util.List;

public class Coche {

    private String matricula;
    private String marca;
    private String modelo;
    private List<Extra> extras;
    private double precio;
    private int idPropietario;

    public Coche(String matricula, String marca, String modelo, List<Extra> extras, double precio, int idPropietario) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.extras = extras;
        this.precio = precio;
        this.idPropietario = idPropietario;
    }

    public Coche(String matricula, String marca, String modelo, List<Extra> extras, double precio) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.extras = extras;
        this.precio = precio;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public List<Extra> getExtras() {
        return extras;
    }

    public double getPrecio() {
        return precio;
    }

    public int getIdPropietario() {
        return idPropietario;
    }

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
