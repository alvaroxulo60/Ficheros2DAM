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
}
