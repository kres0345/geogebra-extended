package org.geogebra.common.main;

import org.geogebra.common.plugin.GgbAPI;

import java.io.*;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LANController {

    enum NetType{
        SERVER,
        CLIENT
    }

    static List<String> commandQueue = new ArrayList<String>();
    static InetAddress ConnectedClient;

    private static Thread currentThread;

    /**
     * Used for adding data to be sent over to client/server.
     * @param data Data to send
     */
    public static void addData(String data) { commandQueue.add(data); }
    public static void sendCommand(String cmd) { commandQueue.add("e:" + cmd); }

    /**
     * Asks server to disconnect :)
     */
    public static void disconnectClient(){ commandQueue.add("dis"); }

    /**
     * @return Weather client is connected.
     */
    public static boolean isConnected() { return ConnectedClient != null; }

    private static void ResetSettings(){
        commandQueue = new ArrayList<String>();
        ConnectedClient = null;
    }

    public static void StartServer(){
        try{
            if(currentThread != null && !currentThread.isInterrupted()){
                currentThread.interrupt();
            }
        }catch (Exception e){}
        ResetSettings();

        LANThread lanThread = new LANThread();
        lanThread.netType = NetType.SERVER;

        currentThread = new Thread(lanThread);
        currentThread.start();
    }

    public static void ConnectClient(String target){
        try{
            if(currentThread != null && !currentThread.isInterrupted())
                currentThread.interrupt();
        }catch (Exception e){ }
        ResetSettings();

        LANThread tLAN = new LANThread();
        tLAN.netType = NetType.CLIENT;

        try{
            tLAN.targetAddress = InetAddress.getByName(target);
        }catch (UnknownHostException e){
            System.out.println("Invalid address");
            return;
        }

        currentThread = new Thread(tLAN);
        currentThread.start();
    }
}
/*
class LANControllerServer implements Runnable{

    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket(1111);
            boolean running = true;
            while(running){
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

}

class LANControllerClientHandler implements Runnable{

    public void run(){

    }
}*/

/*
    public boolean StartServer(String target) {
        try{
            Selector selector = Selector.open();

            ServerSocketChannel geogebraSocket = ServerSocketChannel.open();
            InetSocketAddress geogebraAddr = new InetSocketAddress("localhost", 1111);

            geogebraSocket.bind(geogebraAddr);
            geogebraSocket.configureBlocking(false);

            int ops = geogebraSocket.validOps();
            SelectionKey selectKy = geogebraSocket.register(selector, ops, null);


            // Selects a set of keys whose corresponding channels are ready for I/O operations
            selector.select();

            // token representing the registration of a SelectableChannel with a Selector
            Set<SelectionKey> geogebraKeys = selector.selectedKeys();
            Iterator<SelectionKey> geogebraIterator = geogebraKeys.iterator();

            while (geogebraIterator.hasNext()) {
                System.out.println("Grabbing next socket key pair");
                SelectionKey myKey = geogebraIterator.next();

                System.out.println("Testing whether key channel is ready to receive a new socket client.");
                // Tests whether this key's channel is ready to accept a new socket connection
                if (myKey.isAcceptable()) {
                    //SocketChannel geogebraClient = geogebraSocket.accept();
                    System.out.println("Awaiting socket accept.");
                    geogebraClient = geogebraSocket.accept();
                    System.out.println("Socket accepted.");

                    // Adjusts this channel's blocking mode to false
                    geogebraClient.configureBlocking(false);

                    // Operation-set bit for read operations
                    geogebraClient.register(selector, SelectionKey.OP_READ);
                    System.out.println("Connection Accepted: " + geogebraClient.getLocalAddress());

                    // Tests whether this key's channel is ready for reading
                } else if (myKey.isReadable()) {
                    SocketChannel geogebraClient = (SocketChannel) myKey.channel();
                    ByteBuffer geogebraBuffer = ByteBuffer.allocate(256);
                    geogebraClient.read(geogebraBuffer);
                    String result = new String(geogebraBuffer.array()).trim();

                    System.out.println("Message received: " + result);

                    if(result.startsWith("e:")){
                        System.out.println("Recieved command");
                    }

                    if (result.equals("geogebra")) {
                        geogebraClient.close();
                        System.out.println("\nIt's time to close connection as we got last company name 'geogebra'");
                        System.out.println("\nServer will keep running. Try running client again to establish new connection");
                    }
                }
                geogebraIterator.remove();
            }
            return true;
        }catch (IOException e){
            return false;
        }

    }*/






/*
    private void trueClient() throws IOException {
        InetSocketAddress geogebraAddr = new InetSocketAddress("localhost", 1111);
        //SocketChannel geogebraClient = SocketChannel.open(geogebraAddr);
        geogebraClient = SocketChannel.open(geogebraAddr);

        System.out.println("Connecting to Server on port 1111...");
    }

    private void connectClient() throws IOException {
        InetSocketAddress geogebraAddr = new InetSocketAddress("localhost", 1111);
        //SocketChannel geogebraClient = SocketChannel.open(geogebraAddr);
        geogebraClient = SocketChannel.open(geogebraAddr);

        System.out.println("Connecting to Server on port 1111...");

        ArrayList<String> companyDetails = new ArrayList<String>();

        // create a ArrayList with companyName list
        companyDetails.add("Facebook");
        companyDetails.add("Twitter");
        companyDetails.add("IBM");
        companyDetails.add("Google");
        companyDetails.add("geogebra");

        for (String companyName : companyDetails) {

            byte[] message = new String(companyName).getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            geogebraClient.write(buffer);

            System.out.println("sending: " + companyName);
            buffer.clear();

            // wait for 2 seconds before sending next message
            //Thread.sleep(2000);
        }
        geogebraClient.close();
    }*/

//https://crunchify.com/java-nio-non-blocking-io-with-server-client-example-java-nio-bytebuffer-and-channels-selector-java-nio-vs-io/

    /*
    private SocketChannel geogebraClient;

    private List<String> CommandQueue = new ArrayList<String>();

    public static List<String> GetCommandQueue(){
        return LANController.CommandQueue;
    }

    public LANController(){

    }*/


    /*
    public boolean ConnectClient(String target) {
        try{
            //connectClient();
            trueClient();
            return true;
        }catch (IOException e){
            return false;
        }
    }*/
    /*
    public boolean isConnected() {
        if(geogebraClient == null)
            return false;

        return geogebraClient.isConnected();
    }*/
    /*
    public void DisconnectClient() throws IOException {
        geogebraClient.close();
    }*/

    /*
    public int sendMessage(String msg) throws IOException {
        byte[] bytes = msg.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return geogebraClient.write(buffer);
    }
    public int sendCommand(String cmd) throws IOException {
        return sendMessage("e:" + cmd);
    }*/