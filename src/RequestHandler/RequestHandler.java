package RequestHandler;

import APIServer.Request;
import APIServer.Response;
import com.google.gson.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {

    private Request request;


    private static final String SECCEED = "200";
    private static final String NOT_FOUND = "404";
    private static final String BAD_REQUEST = "400";
    private static final String FAILED = "500";


    public RequestHandler(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
    private static final Path CURRENT_DIR = Paths.get(System.getProperty("user.dir"));

    private record ActionHandler(Object instance, Method method) {}

    private static final Map<String, ActionHandler> actions = new HashMap<>();
    static {
        File servicesPath = Paths.get(CURRENT_DIR.toString(),"src", "Services").toFile();
        File[] serviceFiles = servicesPath.listFiles();
        assert serviceFiles != null;
        for (var serviceFile : serviceFiles){
            String serviceName = serviceFile.getName().split("\\.")[0];
            try {
                Class<?> service = Class.forName("Services."+serviceName);
                Object serviceInstance = service.getMethod("getInstance").invoke(null);
                for(var method : service.getDeclaredMethods()){
                    String actionName = serviceName.split("Service")[0]
                            +'/'
                            +method.getName();
                    actions.put(actionName,new ActionHandler(serviceInstance,method));
                }

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

        }
    }
    public Response handle() {
        Gson gson = new Gson();
        try {
            if(!actions.containsKey(request.getActionName())){
                return new Response(NOT_FOUND,"Service was not found:"+request.getActionName(),new JsonObject());
            }
            ActionHandler action= actions.get(request.getActionName());
            Type[] paramTypes = action.method.getGenericParameterTypes();
            Object[] args = new Object[paramTypes.length];
            try {
                JsonElement jsonElement = gson.fromJson(request.getPayload(), JsonElement.class);
                if (jsonElement.isJsonArray()) {
                    JsonArray jsonArray = jsonElement.getAsJsonArray();
                    for (int i = 0; i < paramTypes.length; i++) {
                        args[i] = gson.fromJson(jsonArray.get(i), paramTypes[i]);
                    }
                }
                else{
                    if (paramTypes.length == 1) {
                        args[0] = gson.fromJson(jsonElement, paramTypes[0]);
                    } else {
                        throw new RuntimeException("Method requires multiple arguments, but JSON is not an array.");
                    }
                }
            }
            catch (JsonSyntaxException e){
                return new Response(BAD_REQUEST,"Bad request:"+request.getActionName(),new JsonObject());
            }

            Object result;
            if (action.method.getReturnType().equals(void.class)) {
                action.method.invoke(action.instance, args);
                result = null;
            } else {
                result = action.method.invoke(action.instance, args);
            }


            Response response = new Response();
            response.setStatus(SECCEED);
            if (result != null) {
                JsonObject payload;
                payload = gson.toJsonTree(result).getAsJsonObject();

                response.setPayload(payload);
            } else {
                response.setMessage("Request handled successfully.");
                response.setPayload(new JsonObject());
            }
            return response;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            Throwable exp = e.getCause();
            return new Response(FAILED,exp.getMessage(),new JsonObject());
        }

    }

}