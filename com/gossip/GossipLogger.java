package com.gossip;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GossipLogger {
    public GossipLogger(){

    }

    public static void info(String log) {
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println("[INFO]" + "[" + timestamp + "] " + log);
    }

    public static void error(String log) {
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println("[INFO]" + "[" + timestamp + "] " + log);
    }
}
