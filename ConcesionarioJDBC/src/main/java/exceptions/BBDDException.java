package exceptions;

public class BBDDException extends Exception {
    /** Crea un error de base de datos con un mensaje explicativo. */
    public BBDDException(String message) {
        super(message);
    }
}
