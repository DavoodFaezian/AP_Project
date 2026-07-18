package RequestHandler;

import APIServer.Request;
import APIServer.Response;
import DTO.LogInDto;
import DTO.CheckPasswordDto;
import DTO.SignUpDto;
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
            case "sign up":
            case "log in": logIn(); break;
            case "confirm password": confirmPassword(); break;
            case "change password": changePassword(); break;
        }

    }

    public void signUp(){
        SignUpDto data = new SignUpDto(request.getPayload());
        UserService service = UserService.getInstance();
        service.signUp(data);
    }

    public void logIn() {
        LogInDto data = new LogInDto(request.getPayload());
        UserService service = UserService.getInstance();
        service.logIn(data);
    }

    public void confirmPassword() {
        CheckPasswordDto data = new CheckPasswordDto(request.getPayload());
        UserService service = UserService.getInstance();
        service.confirmPassword(data);
    }

    public void changePassword() {

    }

}