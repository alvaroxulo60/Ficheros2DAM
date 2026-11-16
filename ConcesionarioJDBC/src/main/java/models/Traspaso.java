package models;

public class Traspaso {
    private int idTraspaso;
    private String matriculaCoche;
    private int idVendedor;
    private int idComprador;
    private double montoEconomico;

    public Traspaso(String mtriculaCoche, int idVendedor, int idComprador, double montoEconomico) {
        this.matriculaCoche = mtriculaCoche;
        this.idVendedor = idVendedor;
        this.idComprador = idComprador;
        this.montoEconomico = montoEconomico;
    }

    public Traspaso(int idTraspaso, String mtriculaCoche, int idVendedor, int idComprador, double montoEconomico) {
        this.idTraspaso = idTraspaso;
        this.matriculaCoche = mtriculaCoche;
        this.idVendedor = idVendedor;
        this.idComprador = idComprador;
        this.montoEconomico = montoEconomico;
    }
}
