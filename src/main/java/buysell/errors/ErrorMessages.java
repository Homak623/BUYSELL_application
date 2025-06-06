package buysell.errors;

public class ErrorMessages {
    private ErrorMessages() {
        throw new UnsupportedOperationException(
            "This is a utility class and cannot be instantiated");
    }

    public static final String PRODUCT_IN_USE = "Product with id %d cannot be "
        + "deleted because it is used in orders";
    public static final String ERROR = "error";
    public static final String USER_NOT_FOUND = "User with id %d not found";
    public static final String ORDER_NOT_FOUND = "Order with id %d not found";
    public static final String PRODUCT_NOT_FOUND = "Product with id %d not found";
    public static final String EMAIL_EXISTS = "User with email %s already exists";
    public static final String NO_VALID_PRODUCTS = "No valid products found for the given IDs";
}
