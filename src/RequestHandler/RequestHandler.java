package RequestHandler;

import APIServer.Request;
import APIServer.Response;
import DTO.AuthenticationDto;
import DTO.ConfirmPasswordDto;
import Services.UserService;

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

    public void handle() {
        String Action = request.getActionName();
        switch (Action) {
            case "sign up", "log in": authenticate(); break;
            case "confirm password": confirmPassword(); break;
            case "change password": changePassword(); break;
        }

    }

    public void authenticate() {
        AuthenticationDto data = new AuthenticationDto(request.getPayload());
        UserService service = UserService.getInstance();
        service.signUp(data);
    }

    public void confirmPassword() {
        ConfirmPasswordDto data = new ConfirmPasswordDto(request.getPayload());
        UserService service = UserService.getInstance();
        service.confirmPassword(data);
    }

    public void changePassword() {

    }

}