package APIServer;

import com.google.gson.JsonObject;

public class Request {

    private String actionName;

    private JsonObject payload;

    public Request(String actionName, JsonObject payload) {
        this.actionName = actionName;
        this.payload = payload;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public JsonObject getPayload() {
        return payload;
    }

    public void setPayload(JsonObject payload) {
        this.payload = payload;
    }
}
