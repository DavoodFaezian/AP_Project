package APIServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements AutoCloseable {

    private static final int PORT = 1234;
    private static final int BACKLOG = 1000; // Increased to prevent Connection Refused

    // Limits threads to prevent out-of-memory crashes under heavy load
    private static final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

    public static void main() {
        try(ServerSocket server = new ServerSocket(PORT, BACKLOG)) {
            System.out.println("Server started on port " + PORT);

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

    @Override
    public void close() throws Exception {
        pool.shutdown();
    }
}