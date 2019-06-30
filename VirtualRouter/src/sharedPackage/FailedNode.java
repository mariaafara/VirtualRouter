/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharedPackage;

import java.io.Serializable;
import java.net.InetAddress;
import sharedPackage.RoutingTableKey;

/**
 *
 * @author maria afara
 */
public class FailedNode implements Serializable{

    InetAddress inetaddress;
    int port;
    String hostname;
////complete failed or entry 
    //failedentry aw failedrouter

    RoutingTableKey Destination,  myKey;
    
    //ana router a m7et l b 
    // aya dest wmin ma7eha  
    public FailedNode(InetAddress inetaddress, String hostname, int port) {
        this.inetaddress = inetaddress;
        this.port = port;
        this.hostname = hostname;
    }

    //////destination=> nextiphost , myKey=>myipHost
    public FailedNode(RoutingTableKey Destination, RoutingTableKey myKey) {
        this.Destination=Destination;
        this.myKey=myKey;
    }
//       public FailedNode(RoutingTableKey nextiphost, RoutingTableKey myiphost) {
//        this.nextiphost=nextiphost;
//        this.myipHost=myipHost;
//    }
//    
    public RoutingTableKey getDestination(){
        return Destination;
    }
    
        public RoutingTableKey getMyKey(){
        return myKey;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "FailedNode{" + "Destination=" + Destination + ", myKey=" + myKey + '}';
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InetAddress getInetaddress() {
        return inetaddress;
    }

    public void setInetaddress(InetAddress inetaddress) {
        this.inetaddress = inetaddress;
    }

}
