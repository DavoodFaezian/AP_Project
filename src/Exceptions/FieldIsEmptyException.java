package Exceptions;

public class FieldIsEmptyException extends RuntimeException {
  public FieldIsEmptyException(String message) {
    super(message);
  }
}
