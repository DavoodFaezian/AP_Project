package APIServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 1234;

    private static final ExecutorService pool = Executors.newCachedThreadPool();

    static void main() {

        try(ServerSocket server = new ServerSocket(PORT)) {
            while (!server.isClosed()) {
                Socket socket = server.accept();
                pool.execute(new ClientHandler(socket));
            }
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}
