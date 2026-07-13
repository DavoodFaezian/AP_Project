package Exceptions;

public class ActionFailedException extends RuntimeException{
    public ActionFailedException (String actionName){
        super(actionName + " failed");
    }
}
