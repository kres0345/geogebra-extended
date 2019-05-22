package org.geogebra.common.main;

import java.io.DataOutputStream;
import java.io.BufferedReader;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class LANController {

    //https://crunchify.com/java-nio-non-blocking-io-with-server-client-example-java-nio-bytebuffer-and-channels-selector-java-nio-vs-io/

    private SocketChannel geogebraClient;

    public LANController(){

    }

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

    }
    public boolean ConnectClient(String target) {
        try{
            //connectClient();
            trueClient();
            return true;
        }catch (IOException e){
            return false;
        }
    }
    public boolean isConnected() {
        if(geogebraClient == null)
            return false;

        return geogebraClient.isConnected();
    }
    public void DisconnectClient() throws IOException {
        geogebraClient.close();
    }

    public int sendMessage(String msg) throws IOException {
        byte[] bytes = msg.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return geogebraClient.write(buffer);
    }
    public int sendCommand(String cmd) throws IOException {
        return sendMessage("e:" + cmd);
    }

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
    }


}
