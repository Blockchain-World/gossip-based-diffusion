package com.gossip;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class Utils {

    // return current time in microsecond
    public static Long currentTS(String mode){
        if (mode.equals("milli")){
            //milli second
            return System.currentTimeMillis();
        }else if (mode.equals("micro")){
            // micro second
            Long cutime = System.currentTimeMillis() * 1000; // micro second
            Long nanoTime = System.nanoTime(); // nano second
            return cutime + (nanoTime - nanoTime / 1000000 * 1000000) / 1000;
        }
        else if (mode.equals("nano")){
            // nano second
            return System.nanoTime();
        }else {
            return System.currentTimeMillis();
        }
    }

    // write data to ledger
    public static boolean writeToLedger(String data){
        FileWriter fw = null;
        try {
            File f = new File("com/gossip/ledger.txt");
            fw = new FileWriter(f, true);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(data);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //return k different node indexes
    public static int[] randomSet(int min, int max, int k){
        Set<Integer> set = new HashSet<Integer>();
        int[] array = new int[k];
        for(;true;){
            //call random
            int num = (int)(Math.random() * (max - min + 1)) + min;
            set.add(num);
            if(set.size() >= k){
                break;
            }
        }
        int i = 0;
        for (int a : set){
            array[i] = a;
            i++;
        }
        return array;
    }

    public static boolean Gossip(String message){
        Socket clientSocket = null;
        PrintWriter out = null;
        // e.g., generate [1,2,3,...,20] but do not contain the node_id,length is 19, index:[0,18]
        List nodes = Utils.generateNodeList();
        // e.g., pick 3 random numbers out of 19
        int[] k = Utils.randomSet(Constant.MIN_NODE, Constant.MAX_NODE - 1, Constant.GOSSIP_DISSEMINATION);
        try{
            for (int ak : k) {
                String nodeName = "node" + nodes.get(ak - 1); // ak : [1, 19], ak-1: [0, 18]
                String targetAddress = GossipNode.alliances.get(nodeName).getAddress();
                GossipLogger.info("Gossip to peer " + targetAddress);
                clientSocket = new Socket(targetAddress, Constant.GOSSIP_PORT);
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(message);
            }
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //generate the nodes ID exclude itself
    public static ArrayList<Integer> generateNodeList(){
        List<Integer> list = new ArrayList<>();
        for (int i = Constant.MIN_NODE; i <= Constant.MAX_NODE; i++){
            if (i != Constant.NODE_ID){
                list.add(i);
            }
        }
        return (ArrayList<Integer>) list;
    }
}
