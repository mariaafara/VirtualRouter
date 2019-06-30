package sharedPackage;

import java.io.Serializable;
import java.net.InetAddress;

/**
 *
 * @author maria afara
 */
public class Neighbor implements Serializable {

    public InetAddress neighborAddress;
    public Integer neighborPort;
    public String neighborname;

    public Neighbor(InetAddress neighborAddress,String neighborname, Integer neighborPort) {
        this.neighborAddress = neighborAddress;
         this.neighborname = neighborname;
        this.neighborPort = neighborPort;
    }



    public String getNeighborname() {
        return neighborname;
    }

    public void setNeighborname(String neighborname) {
        this.neighborname = neighborname;
    }

    public InetAddress getNeighborAddress() {
        return neighborAddress;
    }

    public void setNeighborAddress(InetAddress neighborAddress) {
        this.neighborAddress = neighborAddress;
    }

    public Integer getNeighborPort() {
        return neighborPort;
    }

    public void setNeighborPort(Integer neighborPort) {
        this.neighborPort = neighborPort;
    }

    @Override
    public String toString() {
        return "Neighbor{" + "neighborAddress=" + neighborAddress + ", neighborPort=" + neighborPort + '}';
    }
}
