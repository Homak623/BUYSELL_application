package buysell.errors;

public class CannotDeleteProductException extends RuntimeException {
    public CannotDeleteProductException(String message) {
        super(message);
    }
}
