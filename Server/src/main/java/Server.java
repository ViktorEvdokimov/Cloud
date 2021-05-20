import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public Server() {
        try(ServerSocket srv = new ServerSocket(8189)) {
            System.out.println("Server started...");
            while (true) {
                Socket socket = srv.accept();
                System.out.println("Client accepted...");
                ClientHandler handler = new ClientHandler(socket);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            System.err.println("Server was broken");
        }
    }

    public static void main(String[] args) {
        new Server();
    }

}