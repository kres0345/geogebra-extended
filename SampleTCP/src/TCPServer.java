import java.io.*;
import java.net.*;

class TCPServer {
    public static void main(String argv[]) throws Exception {
        String clientSentence;
        String capitalizedSentence;
        ServerSocket welcomeSocket = new ServerSocket(6789);
        System.out.println("Listing on port: 6789");


        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            System.out.println("BufferedReader");
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            System.out.println("outToClient");

            while(connectionSocket.isConnected()){
                clientSentence = inFromClient.readLine();
                System.out.println("Reading line");
                System.out.println("Received: " + clientSentence);
                capitalizedSentence = clientSentence.toUpperCase() + 'n';
                outToClient.writeBytes(capitalizedSentence);
            }
        }
    }
}
