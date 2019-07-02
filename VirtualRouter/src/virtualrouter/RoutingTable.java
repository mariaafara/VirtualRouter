package virtualrouter;

import javafx.application.Platform;

import sharedPackage.FailedNode;
import sharedPackage.RoutingTableKey;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
//import sharedPackage.ObservaleStringBuffer;

/**
 * this class represents the routing table which is a hash map
 *
 * @author maria afara
 */
public class RoutingTable implements Serializable {

    HashMap<RoutingTableKey, RoutingTableInfo> routingEntries;
    ArrayList<String> strings;
    //transient krmel ma ynb3to lobjects manon serilizable kmn 
    transient final Object lockRoutingTable = new Object();
    transient final Object lockPortconxs = new Object();
    LocalTime myObjDate;

    public RoutingTable() {

        strings = new ArrayList<String>();

        routingEntries = new HashMap<>();
        this.myObjDate = LocalTime.now();
        // Display the current date

    }
//if itcontain this network with the ip and port set the established boolean to true

    public HashMap<RoutingTableKey, RoutingTableInfo> getRoutingEntries() {
        synchronized (lockRoutingTable) {
            return routingEntries;
        }
    }

    public void establishEntry(InetAddress ip, String hostname) {

        synchronized (lockRoutingTable) {
            RoutingTableKey ipHost = new RoutingTableKey(ip, hostname);
            System.out.println("establish entry for " + ip + " " + hostname + "\n");
            routingEntries.get(ipHost).setEstablished(true);

        }
    }

    public void establishEntry(RoutingTableKey ipPort) {
        synchronized (lockRoutingTable) {
            if (routingEntries.containsKey(ipPort)) {
                System.out.println("\n*" + ipPort.toString() + "  exist in routing table and is established now");

                routingEntries.get(ipPort).setEstablished(true);
            } else {
                System.out.println("\n*" + ipPort.toString() + " does not exist in routing table");
            }
        }
    }

///this method return the entryof the given  network or name
    public RoutingTableInfo getEntry(InetAddress ip, String hostname) {
        synchronized (lockRoutingTable) {
            RoutingTableKey ipHost = new RoutingTableKey(ip, hostname);
            return routingEntries.get(ipHost);
        }
    }

    public RoutingTableInfo getEntry(RoutingTableKey ipPort) {
        synchronized (lockRoutingTable) {

            return routingEntries.get(ipPort);
        }
    }

    public RoutingTableInfo getEntry(int portDst) {
        synchronized (lockRoutingTable) {
            for (HashMap.Entry<RoutingTableKey, RoutingTableInfo> entry : routingEntries.entrySet()) {
                if (entry.getValue().getNextHop() == portDst) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    public boolean isEstablishedEntry(InetAddress ip, String hostname) {
        synchronized (lockRoutingTable) {
            RoutingTableKey ipHost = new RoutingTableKey(ip, hostname);
            if (routingEntries.containsKey(ipHost)) {
                System.out.println(ip + " , " + hostname + " in routing table\n");
            } else {
                System.out.println(ip + " , " + hostname + " not in routing table\n");
            }

            return routingEntries.get(ipHost).isEstablished();
        }
    }

    /*
	 * This method adds an entry into the routing table
	 * @para synchronized (lockRoutingTable) {m destIP = destination  IP address
	 * @param nextHop = nextHop IP address
	 * @param cost = Cost to reach the destination
     */
    public void addEntry(InetAddress destIp, String desthostname, RoutingTableKey nextipHost, int nextHop, int cost, int myport, Port portclass, boolean activated, boolean established) throws UnknownHostException {
        synchronized (lockRoutingTable) {
            RoutingTableKey ipPort = new RoutingTableKey(destIp, desthostname);
            this.routingEntries.put(ipPort, new RoutingTableInfo(nextHop, cost, nextipHost, myport, portclass, activated, established));
            this.myObjDate = LocalTime.now();
        }
    }

    public void addEntry(RoutingTableKey ipPort, RoutingTableKey nextipHost, int nextHop, int cost, int myport, Port portclass, boolean activated, boolean established) throws UnknownHostException {
        synchronized (lockRoutingTable) {
            this.myObjDate = LocalTime.now();
            this.routingEntries.put(ipPort, new RoutingTableInfo(nextHop, cost, nextipHost, myport, portclass, activated, established));
        }
    }

    public int getNextHop(int myport) {
        synchronized (lockRoutingTable) {

            // return routingEntries.get(ipPort).nextHop;
            for (HashMap.Entry<RoutingTableKey, RoutingTableInfo> entry : routingEntries.entrySet()) {

                if (entry.getValue().port == myport) {
                    return entry.getValue().nextHop;
                }
            }

            return 0;
        }

    }

    ///next rtKey
    public RoutingTableKey getNextipHost(int myport) {
        synchronized (lockRoutingTable) {

            // return routingEntries.get(ipPort).nextHop;
            for (HashMap.Entry<RoutingTableKey, RoutingTableInfo> entry : routingEntries.entrySet()) {

                if (entry.getValue().port == myport) {
                    return entry.getValue().nextipHost;
                }
            }

            return null;
        }

    }

    public void activateEntry(InetAddress ip, String hostname) {
        synchronized (lockRoutingTable) {
            RoutingTableKey ipHost = new RoutingTableKey(ip, hostname);
            routingEntries.get(ipHost).setActivated(true);

        }
    }

    public void deactivateEntry(InetAddress ip, String hostname) {
        synchronized (lockRoutingTable) {
            RoutingTableKey ipHost = new RoutingTableKey(ip, hostname);
            routingEntries.get(ipHost).setActivated(false);
        }
    }

    /*
        * This method checks if the routing table is formed and filled or not yet
     */
    public boolean isEmptyTable() {
        synchronized (lockRoutingTable) {
            return this.routingEntries.isEmpty();
        }
    }

    /*
        * This method delets an entry from the routing table
     */
    public void deleteEntry(InetAddress ip, String hostname) {
        synchronized (lockRoutingTable) {
            RoutingTableKey ipHost = new RoutingTableKey(ip, hostname);
            this.routingEntries.remove(ipHost);
            this.myObjDate = LocalTime.now();
        }
    }

    /*
        * This method delets an entry from the routing table
     */
    public void deleteEntry(RoutingTableKey ipHost) {
        synchronized (lockRoutingTable) {
            this.myObjDate = LocalTime.now();
            this.routingEntries.remove(ipHost);
        }
    }
//method that deletess the failednode and all the nodes that are reached through this failed node recieved
    //it rturns an arraylist of all the removed entries to broadcast to the neighbors later on in the code

    public ArrayList<FailedNode> deleteFailedNodes(RoutingTableKey dest,
            RoutingTableKey nextipHost, RoutingTableKey myipHost) {
        synchronized (lockRoutingTable) {
            ArrayList<FailedNode> arrayfn = new ArrayList<>();
            System.out.println("**");

            Map<RoutingTableKey, RoutingTableInfo> tempRT = new HashMap<RoutingTableKey, RoutingTableInfo>();
            tempRT.putAll(routingEntries);
            Iterator<Map.Entry<RoutingTableKey, RoutingTableInfo>> itr = tempRT.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<RoutingTableKey, RoutingTableInfo> entry = itr.next();
                if (dest.equals(entry.getKey()) && nextipHost.equals(entry.getValue().getNextipHost())) {
                    //   rt.deleteEntry(entry.getValue().getNextipHost());
                    System.out.println("*before delete");
                    routingEntries.remove(entry.getKey());
                    System.out.println("*after delete");

                    //3m eb3t lentry le m7itaa
                    //     FailedNode newfn = new FailedNode(entry.getKey().getIp(), entry.getKey().getHostname(), entry.getValue().getPort());
                    FailedNode newfn = new FailedNode(dest, myipHost);
                    System.out.println("\n" + newfn);
                    arrayfn.add(newfn);

                }
            }

            this.myObjDate = LocalTime.now();
            return arrayfn;
        }
    }

    public Port getPortClass(int myport) {
        Port p = null;
        for (HashMap.Entry<RoutingTableKey, RoutingTableInfo> entry : routingEntries.entrySet()) {

            if (entry.getValue().port == myport) {
                p = entry.getValue().getPortclass();
            }
        }
        return p;
    }

    /*
	 * this method updates cost to a given destination and its next hop  int nxthopIp,
     */
    public void updateEntry(InetAddress destNtwk, String desthostname, RoutingTableKey nextipHost, int nexthop, Port p, int cost) {
        synchronized (lockRoutingTable) {
            RoutingTableKey ipHost = new RoutingTableKey(destNtwk, desthostname);
            //    RoutingTableKey nextipHost = new RoutingTableKey(nextdestNtwk, nextdesthostname);
            this.myObjDate = LocalTime.now();
            this.routingEntries.get(ipHost).setCost(cost);
            this.routingEntries.get(ipHost).setNextipHost(nextipHost);
            this.routingEntries.get(ipHost).setPortclass(p);
            this.routingEntries.get(ipHost).setNextHop(nexthop);
        }
    }

//this method checks if it contains its port
    public boolean containsPort(int port) {
        synchronized (lockRoutingTable) {
            boolean contains = false;
            for (HashMap.Entry<RoutingTableKey, RoutingTableInfo> entry : routingEntries.entrySet()) {

                if (entry.getValue().port == port) {
                    contains = true;
                }
            }

            return contains;
        }
    }

    public boolean containsNextHop(int port) {
        synchronized (lockRoutingTable) {
            boolean contains = false;
            for (HashMap.Entry<RoutingTableKey, RoutingTableInfo> entry : routingEntries.entrySet()) {

                if (entry.getValue().nextHop == port) {
                    contains = true;
                }
            }

            return contains;
        }
    }

    public boolean isExistandNotActive(InetAddress ip, String hostname) {
        synchronized (lockRoutingTable) {
            boolean contains = false;

            RoutingTableKey ipHost = new RoutingTableKey(ip, hostname);

            //   System.out.println("\n\n* @ and port "+ipPort.ip+" , "+ipPort.port);
            if (routingEntries.containsKey(ipHost)) {
                System.out.println("\n*" + ipHost.toString() + "  exist in routing table ");
            } else {
                System.out.println("\n*" + ipHost.toString() + " does not exist in routing table ");
            }

            return routingEntries.containsKey(ipHost) && !routingEntries.get(ipHost).activated;

        }
    }

    /*
	 * This method prints the routing table entries
     */
    public void printTable(String hint) {
        // creating iterator for HashMap 
        synchronized (this) {
            LocalTime myDate = this.myObjDate;
            String string;
            System.out.println("in print table ");

            Iterator< HashMap.Entry< RoutingTableKey, RoutingTableInfo>> routingEntriesIterator = routingEntries.entrySet().iterator();

            String destAddress, nextipHost;
            // VirtualRouter.buffer.appendText(System.getProperty("line.separator"));
            string = "\n|--------------------------------------------------------------------------------------------------------------------|\n"
                    + " " + hint + "--Last updated " + myDate + "\n"
                    + "|--------------------------------------------------------------------------------------------------------------------|\n"
                    + "Dest Network\t\tnext ip-Host\t\tCost\t\tmyport\t\tnextPort\t\tActivated\t\tEstablished\n"
                    + "|--------------------------------------------------------------------------------------------------------------------|\n";

            Platform.runLater(() -> {
                VirtualRouter.buffer.appendText(string);

            });

            while (routingEntriesIterator.hasNext()) {
                String string2;
                HashMap.Entry<RoutingTableKey, RoutingTableInfo> pair = (HashMap.Entry<RoutingTableKey, RoutingTableInfo>) routingEntriesIterator.next();

                destAddress = pair.getKey().getIp().getHostAddress() + "-" + pair.getKey().getHostname();
                nextipHost = pair.getValue().getNextipHost().getIp().getHostAddress() + "-" + pair.getValue().getNextipHost().getHostname();

                RoutingTableInfo destForwardingInfo = (RoutingTableInfo) pair.getValue();
//bs ntb3 linet address btbi3to 3m berj3 forword slash bas destAddress.getHostName() 3m trj3 aw2et msln one.one.one.

                string2 = "" + destAddress + "\t"
                        + "" + nextipHost + "\t\t"
                        + destForwardingInfo.cost + "\t\t"
                        + "  " + destForwardingInfo.nextHop + "\t\t"
                        + "  " + destForwardingInfo.port + "\t\t\t"
                        + "  " + destForwardingInfo.activated + "\t\t"
                        + "  " + destForwardingInfo.established + "\t\n";
                // routerInterface.append(System.getProperty("line.separator"));
                Platform.runLater(() -> {
                    VirtualRouter.buffer.appendText(string2);

                });
            }
           Platform.runLater(() -> {
                VirtualRouter.buffer.appendText("|--------------------------------------------------------------------------------------------------------------------|\n\n");
            });
        }
    }
}
