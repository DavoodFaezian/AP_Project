package MainClasses;

import java.io.Serializable;
import java.util.UUID;

public abstract class BaseClass<T> implements Serializable {
    protected String id;
    public static String fileName;
    public static String fileHeader;
    public abstract void afterLoad();

    public String getId() {
        return id;
    }

    public BaseClass() {
        //UUID is a java util library that generate un-identical random strings.
        this.id = UUID.randomUUID().toString();
    }
}
