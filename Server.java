import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static ConcurrentHashMap<Integer, PrintWriter> clientWriters = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345); 
            System.out.println("Server is listening...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); 

                PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.put(clientSocket.getPort(), clientWriter);

                BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String username = clientReader.readLine(); 

                String joinMessage = username + " has joined.";
                System.out.println(joinMessage); 
                broadcast(joinMessage, -1);

                ClientHandler clientHandler = new ClientHandler(clientSocket, username);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, int senderPort) {
        for (int port : clientWriters.keySet()) {
            if (port != senderPort) {
                PrintWriter clientWriter = clientWriters.get(port);
                clientWriter.println(message);
            }
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;
    private String username;

    public ClientHandler(Socket clientSocket, String username) {
        this.clientSocket = clientSocket;
        this.username = username;
    }

    @Override
    public void run() {
        try {
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String line;
            while ((line = clientReader.readLine()) != null) {
                System.out.println(username + ": " + line); 
                Server.broadcast(username + ": " + line, clientSocket.getPort());
            }

            System.out.println(username + " has left.");
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
