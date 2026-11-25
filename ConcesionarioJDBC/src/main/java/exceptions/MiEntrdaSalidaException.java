package exceptions;

public class MiEntrdaSalidaException extends Exception {
    /** Crea un error de entrada o salida con un mensaje explicativo. */
    public MiEntrdaSalidaException(String message) {
        super(message);
    }
}
