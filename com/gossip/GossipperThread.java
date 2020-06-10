package com.gossip;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

public class GossipperThread implements Runnable {

    private ExecutorService executor = null;
    private ServerSocket gossipServerSocket = null;
    private String address;
    private int port;

    public GossipperThread(ExecutorService executor, ServerSocket gossipServerSocket, String address, int port){
        this.executor = executor;
        this.gossipServerSocket = gossipServerSocket;
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        GossipLogger.info("Start gossipper thread on " + address + ":" + port);
        try{
            while (true){
                try{
                    Socket gossipListenerThread = gossipServerSocket.accept();
                    GossipLogger.info("Gossip server received data, start to handle...");
                    executor.execute(new GossipperWorkerThread(gossipListenerThread, gossipServerSocket, address, port));
                }catch (SocketException e){
                    e.printStackTrace();
                    break;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
