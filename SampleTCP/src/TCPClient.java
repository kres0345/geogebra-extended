import java.io.*;
import java.net.*;

class TCPClient implements Runnable {

    public void run(){
        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        String sentence;
        String modifiedSentence;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        try{
            Socket s = new Socket("localhost", 1111);
            DataOutputStream outToServer = new DataOutputStream(s.getOutputStream());

            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));

            sentence = inFromUser.readLine();

            outToServer.writeBytes(sentence + 'n');
            modifiedSentence = inFromServer.readLine();

            System.out.println("FROM SERVER: " + modifiedSentence);

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void evalCommand(String cmd){
        System.out.println("Executing: " + cmd);
    }

    public static void main(String[] argv) throws Exception {
        /*
        String sentence;
        String modifiedSentence;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket("localhost", 6789);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        sentence = inFromUser.readLine();
        outToServer.writeBytes(sentence + 'n');
        modifiedSentence = inFromServer.readLine();
        System.out.println("FROM SERVER: " + modifiedSentence);
        clientSocket.close();*/
    }
}
