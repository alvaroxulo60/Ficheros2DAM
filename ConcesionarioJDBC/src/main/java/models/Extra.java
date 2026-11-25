package models;

public class Extra {
    private String nombre;

    /** Crea un extra con el nombre indicado. */
    public Extra(String nombre) {
        this.nombre = nombre;
    }

    /** Devuelve el nombre del extra. */
    public String getNombre() {
        return nombre;
    }

    /** Devuelve el nombre del extra en forma de texto. */
    @Override
    public String toString() {
        return "Extra{" +
                "nombre='" + nombre + '\'' +
                '}';
    }
}
