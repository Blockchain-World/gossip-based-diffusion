package com.gossip;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GossipNode {

    ExecutorService executor;
    ServerSocket serverSocket;
    ServerSocket gossipServerSocket;
    String address;
    public static HashMap<String, GossipMember> alliances;
    public static String localIPAddress;

    public GossipNode(){
        initializeNode();
    }

    private void initializeNode(){

        alliances = new HashMap<>();

        executor = Executors.newCachedThreadPool();
        //Update alliance information, current is static
        Properties prop = new Properties();
        String localAddress = null;
        try {
            InputStream in = new BufferedInputStream(new FileInputStream("com/gossip/nodes.properties"));
            prop.load(in);
            Iterator<String> it = prop.stringPropertyNames().iterator();
            while (it.hasNext()){
                String key = it.next();
                System.out.println(key + ":" + prop.getProperty(key));
                if (key.equals("localhost")){
                    localAddress = prop.getProperty(key);
                    localIPAddress = localAddress;
                }else {
                    alliances.put(key, new GossipMember(prop.getProperty(key), Constant.SEED_PORT));
                }
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        this.address = localAddress;
        //test alliance
        Iterator ite = alliances.entrySet().iterator();
        while (ite.hasNext()){
            Map.Entry entry = (Map.Entry)ite.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            System.out.println("key: " + key);
            System.out.println("value: " + value.toString());
        }
    }

    public void run(){
        try{
            serverSocket = new ServerSocket(Constant.SEED_PORT);
            gossipServerSocket = new ServerSocket(Constant.GOSSIP_PORT);

            // Thread listening on seed port 30001, put received data to the buffer
            Thread tListenerThread;
            tListenerThread = new Thread(new GossipListenerThread(executor, serverSocket, address, Constant.SEED_PORT));
            tListenerThread.start();

            // Thread for listening on gossip port 40001
            Thread tGossiperThread;
            tGossiperThread = new Thread(new GossipperThread(executor, gossipServerSocket, address, Constant.GOSSIP_PORT));
            tGossiperThread.start();

            // Thread for scanning buffer on seed nodes then gossip
            Thread tBatchThread;
            tBatchThread = new Thread(new GossipBatch(executor));
            tBatchThread.start();

            //Thread for fetching data from buffer and update to ledger
            //Thread tFetchThread;
            //tFetchThread = new Thread(new GossipFetchUpdate(executor));
            //tFetchThread.start();

            // Wait for threads to finish
            try {
                tListenerThread.join();
                tGossiperThread.join();
                tBatchThread.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //Start
        GossipNode node = new GossipNode();
        node.run();
    }
}
