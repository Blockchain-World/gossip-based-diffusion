package com.gossip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GossipWorkerThread implements Runnable {

    Socket listenerSocket;
    ServerSocket serverSocket;
    String address;
    int port;
    BufferedReader br;

    public GossipWorkerThread(Socket listenerSocket, ServerSocket serverSocket,  String address, int port){
        this.listenerSocket = listenerSocket;
        this.serverSocket = serverSocket;
        this.address = address;
        this.port = port;
    }

    private void tryReconnect(){
        try {
            //serverSocket.close();
            listenerSocket = null;
            System.gc();
            listenerSocket = serverSocket.accept();
            System.out.println("Connection is reset...");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
            GossipLogger.info("Start worker thread...");
            br = new BufferedReader(new InputStreamReader(listenerSocket.getInputStream()));
            String seed_data;
            if ((seed_data = br.readLine()) != null) {
                GossipLogger.info("Seed nodes received: " + "\"" + seed_data + "\"" + ", put to buffer...");
                synchronized (this){
                    Constant.SEED_QUEUE.offer(seed_data);
                }
            }
            GossipLogger.info("Current SEED QUEUE length is: " + Constant.SEED_QUEUE.size());
            //close
            listenerSocket.close();
            //GossipLogger.info("Finish worker thread");
        }catch (IOException e){
            e.printStackTrace();
            //tryReconnect();
        }
    }
}
