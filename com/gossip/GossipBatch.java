package com.gossip;

import java.util.concurrent.ExecutorService;

public class GossipBatch implements Runnable {

    ExecutorService executor;

    public GossipBatch(ExecutorService executor){
        this.executor = executor;
    }

    @Override
    public void run() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                GossipLogger.info("Start batch listening thread...");
                try{
                    while (true){
                        //Thread.sleep(500);
                        //GossipLogger.info("Seed queue size: " + Constant.SEED_QUEUE.size());
                        int seedQueueSize = Constant.SEED_QUEUE.size();
                        StringBuilder sb = new StringBuilder();
                        if (seedQueueSize >= Constant.BATCH_SIZE){
                            synchronized (Constant.SEED_QUEUE){
                                GossipLogger.info("Start to batch...");
                                for (int i = 0; i < Constant.BATCH_SIZE; i++){
                                    String temp = Constant.SEED_QUEUE.poll();
                                    if(i == (Constant.BATCH_SIZE - 1)){
                                        sb.append(temp);
                                    }else {
                                        sb.append(temp).append("^_^");
                                    }
                                }
                            }
                        }
                        // Disseminate the batch to the gossip port 40001
                        if (!sb.toString().equals("")){
                            GossipLogger.info("Start to gossip to peers, message: " + sb.toString());
                            if(Constant.GOSSIP_MODE.equals("gossip")){
                                GossipLogger.info("Gossip to random peers");
                                // listen to message from client on port 30001, disseminate to other nodes on port 40001
                                if(Utils.Gossip(sb.toString())){
                                    // Record the timestamp when start to gossip
                                    Utils.writeToLedger("[seed]: " + Utils.currentTS("micro").toString() + " " + sb.toString());
                                    GossipLogger.info("Gossip successfully.");
                                }else {
                                    GossipLogger.error("Gossip failed.");
                                }
                            }else {
                                GossipLogger.error("Gossip mode is wrong.");
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
