package com.gossip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class GossipperWorkerThread implements Runnable {
    Socket gossipListenerSocket;
    ServerSocket gossipServerSocket;
    String address;
    int port;
    BufferedReader gossipBr;

    public GossipperWorkerThread(Socket gossipListenerSocket, ServerSocket gossipServerSocket, String address, int port){
        this.gossipListenerSocket = gossipListenerSocket;
        this.gossipServerSocket = gossipServerSocket;
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        try{
            GossipLogger.info("Start gossiper worker thread...");
            gossipBr = new BufferedReader(new InputStreamReader(gossipListenerSocket.getInputStream()));
            String gossip_data;
            if((gossip_data = gossipBr.readLine()) != null){
                GossipLogger.info("Gossiper server received: " + "\"" + gossip_data + "\"");
                if(Constant.GOSSIP_MODE.equals("gossip")){
                    // when receive it, store it to localCache
                    synchronized (Constant.localCache){
                        GossipLogger.info("Local Cache contains " + gossip_data + ": " + Constant.localCache.containsKey(gossip_data));
                        if (!Constant.localCache.containsKey(gossip_data)){
                            // put message to localCache
                            Constant.localCache.put(gossip_data, 1);
                            // forward message
                            Utils.Gossip(gossip_data);
                            // write message to file
                            Utils.writeToLedger("[gossip]: " + Utils.currentTS("micro") + " " + gossip_data + " " + "1");
                        }else {
                            // if localCache contains such gossip message but the counter is not over t
                            if (Constant.localCache.get(gossip_data) < Constant.t){
                                // forward message
                                Utils.Gossip(gossip_data);
                                // update the counter for message
                                Constant.localCache.put(gossip_data, Constant.localCache.get(gossip_data) + 1);
                                // write message to file
                                Utils.writeToLedger("[gossip]: " + Utils.currentTS("micro") + " " +
                                        gossip_data + " " + Constant.localCache.get(gossip_data));
                            }else {
                                if(!Constant.stable){
                                    // if the gossip message is forwarded more than t times, then reach "stable" status, do not forward
                                    Utils.writeToLedger("[Stable]: " + Utils.currentTS("micro") + " " +
                                            gossip_data + " " + Constant.localCache.get(gossip_data));
                                    Constant.stable = true;
                                }/*else{
                                    System.exit(0);
                                }*/
                            }
                        }
                    }
                }else{
                    GossipLogger.error("Gossip mode is wrong.");
                }
            }
            gossipListenerSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
