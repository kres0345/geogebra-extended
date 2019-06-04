import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

class TCPServer implements Runnable {

    private List<String> CommandQueue = new ArrayList<String>();

    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket(1111);

            while(true){
                try{
                    Socket s = serverSocket.accept();
                    InetAddress clientAddr = s.getInetAddress();
                    System.out.println("New client");
                    BufferedReader out = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    PrintWriter in = new PrintWriter(s.getOutputStream(), true);

                    String inString;
                    while(!s.isClosed() && s.isConnected()){
                        if ((inString = out.readLine()) != null){
                            switch (inString.split(":")[0]){
                                case "e":
                                    evalCommand(inString.substring(2));
                                    break;
                                case "dis":
                                    disconnectedClientNicely(clientAddr);
                                    s.close();
                                    continue;
                                case "t":
                                    CommandQueue.add("Test");
                                    break;
                            }
                        }
                        if (CommandQueue.size() > 0){
                            System.out.println("Sending command from queue...");
                            in.println("e:" + CommandQueue.get(0));
                            CommandQueue.remove(0);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void evalCommand(String cmd){
        System.out.println("Evaluating command: " + cmd);
    }

    /**
     * Asked to be disconnected.
     * @param clientInfo Client information.
     */
    private void disconnectedClientNicely(InetAddress clientInfo){
        System.out.println("Client disconnected: " + clientInfo.toString());
    }

    public static void main(String argv[]) throws Exception {
        (new Thread(new TCPServer())).start();
        System.out.println("Server is listening...");
    }
}
