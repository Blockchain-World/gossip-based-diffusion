package com.gossip;

public class GossipMember implements Comparable<GossipMember> {

    String address;
    int port;
    long heartbeat;
    long localTime;
    boolean failed;

    public GossipMember(){
        this.heartbeat = 0;
        this.failed = false;
    }

    public GossipMember(String address, int port){
        this.address = address;
        this.port = port;
    }

    public GossipMember(String address, int port, long heartbeat){
        this.address = address;
        this.port = port;
        this.heartbeat = heartbeat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(long heartbeat) {
        this.heartbeat = heartbeat;
    }

    public long getLocalTime() {
        return localTime;
    }

    public void setLocalTime(long localTime) {
        this.localTime = localTime;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public String toString(){
        return address + ":" + String.valueOf(port) + ":" + String.valueOf(heartbeat);
    }

    public String toStringWithLocalTime(){
        return address + ":" + String.valueOf(port) + ":" + String.valueOf(heartbeat) + String.valueOf(localTime);
    }


    @Override
    public int compareTo(GossipMember m) {
        //compare addr
        String[] addr1 = this.getAddress().split("\\.");
        String[] addr2 = m.getAddress().split("\\.");

        for (int i = 0; i < 4; i++){
            if(Integer.parseInt(addr1[i]) > Integer.parseInt(addr2[i])){
                return 1;
            }else if(Integer.parseInt(addr1[i]) < Integer.parseInt(addr2[i])){
                return -1;
            }
        }

        //if address is same
        if (this.getPort() > m.getPort()){
            return 1;
        }else if(this.getPort() < m.getPort()){
            return -1;
        }else {
            return 0;
        }
    }
}
