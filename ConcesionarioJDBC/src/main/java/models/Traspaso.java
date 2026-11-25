package models;

public class Traspaso {
    private int idTraspaso;
    private String matriculaCoche;
    private int idVendedor;
    private int idComprador;
    private double montoEconomico;

    /** Crea un traspaso con los datos de la venta. */
    public Traspaso(String mtriculaCoche, int idVendedor, int idComprador, double montoEconomico) {
        this.matriculaCoche = mtriculaCoche;
        this.idVendedor = idVendedor;
        this.idComprador = idComprador;
        this.montoEconomico = montoEconomico;
    }

    /** Crea un traspaso con sus datos y su identificador. */
    public Traspaso(int idTraspaso, String mtriculaCoche, int idVendedor, int idComprador, double montoEconomico) {
        this.idTraspaso = idTraspaso;
        this.matriculaCoche = mtriculaCoche;
        this.idVendedor = idVendedor;
        this.idComprador = idComprador;
        this.montoEconomico = montoEconomico;
    }
}
