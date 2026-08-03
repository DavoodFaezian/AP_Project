import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.util.concurrent.Executors;

public class SocketTest {

    private static final String HOST = "localhost";
    private static final int PORT = 1234;
    private static Socket socket;
    private static BufferedReader reader;
    private static BufferedWriter writer;
    private static final Gson gson = new Gson();

    @BeforeAll
    public static void setup() {
        Executors.new
    }
}
