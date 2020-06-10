package com.gossip;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class GossipListenerThread implements Runnable {

    ExecutorService executor = null;
    ServerSocket serverSocket = null;
    String address;
    int port;


    public GossipListenerThread(ExecutorService executor, ServerSocket serverSocket, String address, int port){
        this.executor = executor;
        this.serverSocket = serverSocket;
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        GossipLogger.info("Start listener thread on " + address + ":" + port);
        while(true){
            try{
                Socket listenerSocket = serverSocket.accept();
                GossipLogger.info("Seed server receive data, start a thread to handle...");
                executor.execute(new GossipWorkerThread(listenerSocket, serverSocket, address, port));
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
