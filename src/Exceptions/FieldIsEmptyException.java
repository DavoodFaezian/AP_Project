package Exceptions;

public class FieldIsEmptyException extends RuntimeException {
    public FieldIsEmptyException(String message,String fieldName) {
        //Send the message and the field name to client.
        super(message);
    }
}
