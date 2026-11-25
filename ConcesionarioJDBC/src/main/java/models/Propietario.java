package models;

public class Propietario {
    private int idPropietario;
    private String dni;
    private String nombre;
    private String apellidos;
    private String telefono;

    /** Crea un propietario con sus datos personales. */
    public Propietario(String dni, String nombre, String apellidos, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
    }

    /** Crea un propietario con sus datos y su identificador. */
    public Propietario(int idPropietario, String dni, String nombre, String apellidos, String telefono) {
        this.idPropietario = idPropietario;
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
    }


    /** Devuelve el DNI del propietario. */
    public String getDni() {
        return dni;
    }

    /** Devuelve el nombre del propietario. */
    public String getNombre() {
        return nombre;
    }

    /** Devuelve los apellidos del propietario. */
    public String getApellidos() {
        return apellidos;
    }

    /** Devuelve el teléfono del propietario. */
    public String getTelefono() {
        return telefono;
    }

    /** Devuelve el identificador del propietario. */
    public int getIdPropietario() {
        return idPropietario;
    }

    /** Devuelve los datos del propietario en forma de texto. */
    @Override
    public String toString() {
        return "Propietario{" +
                "idPropietario=" + idPropietario +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
