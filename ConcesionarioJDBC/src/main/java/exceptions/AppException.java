package exceptions;

public class AppException extends Exception {
    /** Crea un error de la aplicación con un mensaje explicativo. */
    public AppException(String message) {
        super(message);
    }
}
