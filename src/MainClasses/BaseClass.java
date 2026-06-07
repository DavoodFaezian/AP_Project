package MainClasses;

import java.io.Serializable;
import java.util.UUID;

public abstract class BaseClass<T> implements Serializable {
    protected String id;

    public String getId() {
        return id;
    }

    public BaseClass() {
        //UUID is a java util library that generates un-identical random strings.
        this.id = UUID.randomUUID().toString();
    }
}
