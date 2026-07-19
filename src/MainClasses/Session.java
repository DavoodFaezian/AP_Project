package MainClasses;

import java.util.UUID;

public class Session extends BaseClass<Session> {

    String userId;

    String token;

    public Session(String userId) {
        this.userId = userId;
        this.token = UUID.randomUUID().toString();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
