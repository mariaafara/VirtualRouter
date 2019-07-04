package virtualrouter;
//localtest2

import configuration.ConfigurationInterface;
import sharedPackage.FailedNode;
import java.io.IOException;
import sharedPackage.RoutingTableKey;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
//import sharedPackage.ObservaleStringBuffer;

/**
 * Router Class
 *
 * @descrip This class represents a router. It has connections between different
 * routers and maybe p c s. once a router is created its i p and ports are
 * assigned to it .Then is start listing and allowing connection on and to its
 * ports. after that when it activates the routing protocol its own routing
 * service instance is created along with its routing table then Start assign
 * the networks(directly connected) : -add to its neighbors -add to its RT -open
 * the t c p socket c n x with neighbor's port After configuration of routing
 * protocol is done start broadcasting...
 *
 * @author maria afara
 *
 */
public class Router extends UnicastRemoteObject implements ConfigurationInterface {

    static String currentHostIpAddress = null;
    String hostname;

    static InetAddress ipAddress;

    public RoutingTable routingTable;

    final Object lockRouter = new Object();

    PortConxs portConxs;

    ArrayList<RoutingTableKey> neighbors;
    ArrayList<RoutingTableKey> establishedneighbors;

    RoutingService routingService;

    public Router(String hostname) throws RemoteException, UnknownHostException {
        super();
        System.out.println("my local host ------> " + InetAddress.getLocalHost());

        neighbors = new ArrayList<RoutingTableKey>();
        establishedneighbors = new ArrayList<RoutingTableKey>();
        portConxs = new PortConxs();

        routingTable = new RoutingTable();
        if (InetAddress.getByName(getCurrentEnvironmentNetworkIp()) == null) {
            this.ipAddress = InetAddress.getLocalHost();
        } else {
            this.ipAddress = InetAddress.getByName(getCurrentEnvironmentNetworkIp()); //InetAddress.getLocalHost();

        }
        this.hostname = hostname;

    }

    public String getCurrentEnvironmentNetworkIp() {

        if (currentHostIpAddress == null) {
            Enumeration<NetworkInterface> netInterfaces = null;
            try {
                netInterfaces = NetworkInterface.getNetworkInterfaces();

                while (netInterfaces.hasMoreElements()) {
                    NetworkInterface ni = netInterfaces.nextElement();
                    //System.out.println(ni.getName());
                    if (!ni.getName().contains("wlan")) {
                        continue;
                    }
                    Enumeration<InetAddress> address = ni.getInetAddresses();
                    while (address.hasMoreElements()) {
                        InetAddress addr = address.nextElement();
                        //                      log.debug("Inetaddress:" + addr.getHostAddress() + " loop? " + addr.isLoopbackAddress() + " local? "
                        //                            + addr.isSiteLocalAddress());
                        if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()
                                && !(addr.getHostAddress().indexOf(":") > -1)) {
                            //System.out.println(addr);
                            currentHostIpAddress = addr.getHostAddress();
                            return currentHostIpAddress;
                        }
                    }
                }
                if (currentHostIpAddress == null) {
                    currentHostIpAddress = "127.0.0.1";
                }

            } catch (SocketException e) {
//                log.error("Somehow we have a socket error acquiring the host IP... Using loopback instead...");
                currentHostIpAddress = "127.0.0.1";
            }
        }
        return currentHostIpAddress;
    }

    @Override
    public String getHostname() {
        return this.hostname;
    }

    @Override
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public void initializeConnection(int port, InetAddress neighboraddress, String neighborhostname, int neighborport) {
        synchronized (this) {
            if (!portConxs.containsPort(port)) {
//                strings.add("*This port does not exists");
//                VirtualRouter.printToScreen(strings);
//                strings.clear();
                //  VirtualRouter.buffer.appendText(System.getProperty("line.separator"));
                System.out.println("*This port does not exists");
                return;
            }
            neighbors.add(new RoutingTableKey(neighboraddress, neighborhostname));
            portConxs.getPortInstance(port).connect(neighboraddress, neighborhostname, neighborport);
        }
    }

    @Override
    public void initializePort(int port) {
        synchronized (this) {
            if (portConxs.containsPort(port)) {
//                strings.add("*This port exists");
//                VirtualRouter.printToScreen(strings);
//                strings.clear();
                //VirtualRouter.buffer.appendText(System.getProperty("line.separator"));
                System.out.println("*This port exists");
                return;
            }
            Port portclass = new Port(port, hostname, routingTable);

            portConxs.addPort(port, portclass);//3m syv 3ndee lport

            portclass.start();
        }

    }
    int k = 0;

    @Override
    public void initializeRoutingProtocol(ArrayList<RoutingTableKey> networks) {

        routingService = new RoutingService(routingTable, networks);
        routingService.start();

        for (int i = 0; i < networks.size(); i++) {

            if (routingTable.routingEntries.containsKey(networks.get(i)) && routingTable.routingEntries.get(networks.get(i)).activated) {
                routingTable.establishEntry(networks.get(i));
                establishedneighbors.add(networks.get(i));
            }
        }

        Platform.runLater(() -> {
            VirtualRouter.buffer.appendText("initializeRoutingProtocol " + k);
        });

        System.out.println("*initializeRoutingProtocol");
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void disconnet() {
        if (establishedneighbors.size() > 0) {
            FailedNode mefn = new FailedNode(new RoutingTableKey(ipAddress, hostname), new RoutingTableKey(ipAddress, hostname));

            for (HashMap.Entry<RoutingTableKey, RoutingTableInfo> entry2 : routingTable.routingEntries.entrySet()) {

                if (entry2.getValue().cost == 1) {
                    try {
                        /////hon/////
                        //lezm eb3t lkel jar eno m7eet lkel                        

                        System.out.print("\n*broadcast ");
                        ///hyda be 2lb tene loop
                        int i = 0;
                        for (HashMap.Entry<RoutingTableKey, RoutingTableInfo> entry3 : routingTable.routingEntries.entrySet()) {
                            FailedNode newfn = new FailedNode(entry3.getKey(), new RoutingTableKey(ipAddress, hostname));
                            if (i == 0) {
                                entry2.getValue().portclass.getOos().writeObject(mefn);
                            }
                            i++;
                            entry2.getValue().portclass.getOos().writeObject(newfn);
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            routingService.routingTableBroadcast.stopBroadcast();
            Platform.runLater(() -> {
                VirtualRouter.buffer.appendText("Stopping broadcasting\n");
            });
            for (HashMap.Entry<RoutingTableKey, RoutingTableInfo> entry2 : routingTable.routingEntries.entrySet()) {

                if (entry2.getValue().cost == 1) {
                    try {
                        ///////////////stop recieving then stop cnxs (close socket)
                        Reciever reciver = entry2.getValue().portclass.reciever;
                        ///////////////

                        reciver.stopRecieving();
                        Platform.runLater(() -> {
                            VirtualRouter.buffer.appendText("Closing cnx with " + entry2.getKey() + "\n");
                        });

                        System.out.print("\n*closing cnx with " + entry2.getKey());
                        entry2.getValue().portclass.getSocket().close();
                        entry2.getValue().portclass.portConnectionWait.stopWaitingForConnection();
                        //stoping reciving on the adjacent ports where the cnx between them is stoped(finished) due to the failure in one node

                    } catch (IOException ex) {
                        Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    @Override
    public InetAddress getLocalHost() throws RemoteException {
        return this.ipAddress;
    }

    @Override
    public boolean checkPort(int port) throws RemoteException {
        return portConxs.containsPort(port);
    }

    @Override
    public boolean checkNeighbor(InetAddress neghip, String neighname) throws RemoteException {
        RoutingTableKey net = new RoutingTableKey(neghip, neighname);
        boolean isneighbor = false;
        for (RoutingTableKey neighbor : neighbors) {

            if (net.equals(neighbor)) {
                isneighbor = true;
                //  buffer.append("--is neigh--");
            } else {
                isneighbor = false;

            }
        }
        return isneighbor;
    }

    @Override
    public boolean checkEstablishedNeighbor(InetAddress neghip, String neighname) throws RemoteException {
        RoutingTableKey net = new RoutingTableKey(neghip, neighname);
        boolean isestablishedalready = false;
        for (RoutingTableKey establishedneighbor : establishedneighbors) {
            if (establishedneighbor.equals(net)) {
                //already established
                isestablishedalready = true;

            }

        }
        return isestablishedalready;
    }

}
