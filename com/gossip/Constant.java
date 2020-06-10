package com.gossip;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

public class Constant {
    public static final int NODE_ID = 1; //current node ID
    public static final int SEED_DISSEMINATION = 7; // the number of connected seed nodes (sensor -> seed nodes)
    public static final int GOSSIP_DISSEMINATION = 7; // the number of connected peer nodes (gossip)
    public static final int MIN_NODE = 1;
    public static final int MAX_NODE = 20; // the number of nodes
    public static final int SEED_PORT = 30001; // the port on seed nodes
    public static final int GOSSIP_PORT = 40001; // the port for gossip among peer nodes
    public static final int BATCH_SIZE = 1; //start to construct batch from butter
    public static final String GOSSIP_MODE = "gossip";
    public static boolean stable = false;
    public static int t = 20; // the upper bound times that a message is forwarded
    public static int CLIENT_SENT = 10; // the number of data sent by client
    public static volatile Queue<String> SEED_QUEUE = new ArrayDeque<>();
    public static volatile HashMap<String, Integer> localCache = new HashMap<>(); // key: gossip message, value: the number of times that the message is forwarded
}
