import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        try {
            Socket socket = new Socket("localhost", 12345); 
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter clientWriter = new PrintWriter(outputStream, true);
            clientWriter.println(username); 

            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while ((userInput = userInputReader.readLine()) != null) {
                PrintWriter messageWriter = new PrintWriter(socket.getOutputStream(), true);
                messageWriter.println(userInput); 
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
