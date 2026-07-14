package RequestHandler;

import APIServer.Request;
import APIServer.Response;

public class RequestHandler {

    private Request request;

    private Response response;

    public RequestHandler(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
