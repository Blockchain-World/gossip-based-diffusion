package com.gossip;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class GossipClient {

    Socket clientSocket;
    PrintWriter out;
    HashMap<String, String> alliances;

    public GossipClient(){

        alliances = new HashMap<>();
        //Update alliance information
        Properties prop = new Properties();
        String localAddress;
        try {
            InputStream in = new BufferedInputStream(new FileInputStream("com/gossip/nodes.properties"));
            prop.load(in);
            for (String key : prop.stringPropertyNames()) {
                //System.out.println(key + ":" + prop.getProperty(key));
                if (key.equals("localhost")) {
                    localAddress = prop.getProperty(key);
                    GossipLogger.info("Local IP address: " + localAddress);
                } else {
                    alliances.put(key, prop.getProperty(key));
                }
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
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
            Long startTime = Utils.currentTS("micro");
            Utils.writeToLedger("[client]: " + startTime.toString());
            int[] seed_index = Utils.randomSet(Constant.MIN_NODE, Constant.MAX_NODE, Constant.SEED_DISSEMINATION);
            for (int j = 0; j < Constant.CLIENT_SENT; j++){
                for (int i = 0; i < seed_index.length; i++){
                    System.out.println("seed_index[" + i + "]: " + seed_index[i]);
                    String targetAddress = alliances.get("node" + seed_index[i]);
                    clientSocket = new Socket(targetAddress, Constant.SEED_PORT);
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("hello" + (j + 1));
                    GossipLogger.info("Client sends to " + targetAddress + ":" + Constant.SEED_PORT + "--> " + "hello" + (j + 1));
                }
                //Thread.sleep(500);
            }
            clientSocket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                if (out != null)
                    out.close();
                if (clientSocket != null)
                    clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        GossipClient cli = new GossipClient();
        cli.run();
    }
}
