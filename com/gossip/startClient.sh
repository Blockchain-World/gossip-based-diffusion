#!/bin/bash
sudo rm -rf *.class
javac *.java
cd ../..
java com.gossip.GossipClient