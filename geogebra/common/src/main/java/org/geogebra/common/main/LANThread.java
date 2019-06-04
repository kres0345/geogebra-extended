package org.geogebra.common.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class LANThread implements Runnable {

    boolean tcpServer = true;
    InetAddress targetAddress;
    private int netPort = 33231;

    private void evalCommand(String cmd){
        System.out.println("Evaluating: " + cmd);
    }
    private void clientDisconnectedNicely(){
        try{
            System.out.println("Client disconnected: " + LANController.ConnectedClient.toString());
        }catch (NullPointerException e){ e.printStackTrace(); }
    }
    private void clientConnected(){
        try{
            System.out.println("New client: " + LANController.ConnectedClient.toString());
        }catch (NullPointerException e){ e.printStackTrace(); }
    }

    public void run(){
        if (tcpServer) {  // TCP Server
            try{
                ServerSocket serverSocket = new ServerSocket(netPort);

                while(!Thread.currentThread().isInterrupted()){
                    LANController.ConnectedClient = null;
                    Socket s = serverSocket.accept();
                    LANController.ConnectedClient = s.getInetAddress();
                    clientConnected();

                    BufferedReader out = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    PrintWriter in = new PrintWriter(s.getOutputStream(), true);

                    String inString;
                    while(!Thread.currentThread().isInterrupted() && !s.isClosed() && s.isConnected()){
                        while(LANController.commandQueue.size() > 0){
                            System.out.println("Sending command from queue...");
                            in.println("e:" + LANController.commandQueue.get(0));
                            LANController.commandQueue.remove(0);
                        }
                        if ((inString = out.readLine()) != null){
                            switch (inString.split(":")[0]){
                                case "e":
                                    evalCommand(inString.substring(2));
                                    break;
                                case "dis":
                                    clientDisconnectedNicely();
                                    s.close();
                                    continue;
                                case "t":
                                    LANController.addData("Test");
                                    break;
                            }
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        else {  // TCP Client
            try{
                while(!Thread.currentThread().isInterrupted()){
                    LANController.ConnectedClient = null;
                    Socket s;
                    try{
                        s = new Socket(targetAddress, netPort);
                    }catch (Exception e){
                        System.out.println("Cannot connect to target port.");
                        return;
                    }
                    LANController.ConnectedClient = s.getInetAddress();
                    System.out.println("So far..");

                    BufferedReader out = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    PrintWriter in = new PrintWriter(s.getOutputStream(), true);

                    Thread commandTransmitter = new Thread(new ListenCommands(out, in));

                    String inString;
                    while(!Thread.currentThread().isInterrupted() && !s.isClosed() && s.isConnected()){
                        while(LANController.commandQueue.size() > 0) {
                            System.out.println("Sending command from queue...");
                            in.println(LANController.commandQueue.get(0));
                            LANController.commandQueue.remove(0);
                        }
                        if ((inString = out.readLine()) != null){
                            switch (inString.split(":")[0]){
                                case "e":
                                    evalCommand(inString.substring(2));
                                    break;
                                case "dis":
                                    clientDisconnectedNicely();
                                    s.close();
                                    continue;
                                case "t":
                                    LANController.addData("Test");
                                    break;
                            }
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}

class ListenCommands implements Runnable{

    private BufferedReader out;
    private PrintWriter in;

    ListenCommands(BufferedReader out, PrintWriter in){
        this.out = out;
        this.in = in;
    }

    public void run(){
        while(true) {
            if(LANController.commandQueue.size() > 0) {
                System.out.println("Sending command from queue...");
                in.println(LANController.commandQueue.get(0));
                LANController.commandQueue.remove(0);
            }
        }
    }

}