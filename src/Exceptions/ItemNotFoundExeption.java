package Exceptions;

public class ItemNotFoundExeption extends RuntimeException {
    public ItemNotFoundExeption(String className,String id) {
        super("No "+className+" with id of "+id+" was found.");
    }
}
