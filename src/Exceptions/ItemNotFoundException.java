package Exceptions;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String className,String id) {
        super("No "+className+" with id of "+id+" was found.");
    }
}
