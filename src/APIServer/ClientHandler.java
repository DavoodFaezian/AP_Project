package APIServer;

import RequestHandler.RequestHandler;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {

    private final Socket socket;

    private final Gson gson = new Gson();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            )
        ) {
            String line;
            while ((line = reader.readLine()) != null) {

                line = line.trim();

                Request request = gson.fromJson(line , Request.class);
                RequestHandler handler = new RequestHandler(request);

                Response response = handler.handle();

                String responseString = gson.toJson(response);

                writer.write(responseString);
                writer.newLine();
                writer.flush();
            }
        } catch (SocketException e) {
            System.out.println("Client disconnected");
        } catch (IOException e) {
            System.out.println("Error handling client");
        }
    }
}
