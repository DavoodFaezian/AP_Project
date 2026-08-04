package APIServer;

import com.google.gson.JsonObject;

public class Response {

    private String status;

    private String message;

    private JsonObject payload;

    public Response() {

    }

    public Response(String status,String message, JsonObject payLoad) {
        this.status = status;
        this.message = message;
        this.payload = payLoad;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonObject getPayload() {
        return payload;
    }

    public void setPayload(JsonObject payload) {
        this.payload = payload;
    }
}
