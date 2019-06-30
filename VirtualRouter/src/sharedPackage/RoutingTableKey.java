/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharedPackage;

import java.io.Serializable;
import java.net.InetAddress;

/**
 *
 * @author maria afara
 */
public class RoutingTableKey implements Serializable  {

    String hostname;
    InetAddress ip;

    public RoutingTableKey(InetAddress ip, String hostname) {
        this.hostname=hostname;
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String toString() {
        return "RoutingTableKey{" + "hostname=" + hostname + ", ip=" + ip + '}';
    }

  

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        RoutingTableKey other = (RoutingTableKey) o;
        if (!this.hostname.equals(other.hostname)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        // uses roll no to verify the uniqueness 
        // of the object of Student class 

        int ans = ip.hashCode() + hostname.hashCode();
        return ans;  //To change body of generated methods, choose Tools | Templates.
    }

}
