package RequestHandler;

import APIServer.Request;
import APIServer.Response;
import DTO.ChangePasswordDto;
import DTO.LogInDto;
import DTO.SignUpDto;
import Services.UserService;
import com.google.gson.Gson;

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
            case "change password": changePassword(); break;
        }

    }

    public void signUp(){
        Gson gson = new Gson();
        SignUpDto data = gson.fromJson(request.getPayload() , SignUpDto.class);
        UserService service = UserService.getInstance();
        service.signUp(data);
    }

    public void logIn() {
        Gson gson = new Gson();
        LogInDto data = gson.fromJson(request.getPayload() , LogInDto.class);
        UserService service = UserService.getInstance();
        service.logIn(data);
    }

    public void changePassword() {
        Gson gson = new Gson();
        ChangePasswordDto data = gson.fromJson(request.getPayload() , ChangePasswordDto.class);
        UserService service = UserService.getInstance();
        service.changePassword(data);
    }

}