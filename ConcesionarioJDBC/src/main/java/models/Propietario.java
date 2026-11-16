package models;

public class Propietario {
    private int idPropietario;
    private String dni;
    private String nombre;
    private String apellidos;
    private String telefono;

    public Propietario(String dni, String nombre, String apellidos, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
    }

    public Propietario(int idPropietario, String dni, String nombre, String apellidos, String telefono) {
        this.idPropietario = idPropietario;
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
    }
}
