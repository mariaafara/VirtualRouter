/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrouter;

import java.io.IOException;
import sharedPackage.RoutingTableKey;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 * This Thread updates router ' s routing table
 *
 * @author maria afara
 */
public class RoutingTableUpdate extends Thread {

    private final RoutingTable recievedroutingtable;
    private final int myport;
    private ObjectOutputStream oos;
    private RoutingTable rt;
    int recievedport;
    String hostname;
    Port myPP;

    RoutingTableKey destAddress;

    public RoutingTableUpdate(RoutingTable recievedroutingtable, String hostname, int myport, ObjectOutputStream oos, RoutingTable rt, Port myPP) {

        this.recievedroutingtable = recievedroutingtable;
        this.myport = myport;
        this.oos = oos;
        this.rt = rt;
        this.hostname = hostname;
        this.myPP = myPP;
    }

    /*
        * This method updates router ' s routing table based on  routing table received from the neighbor
     */
    @Override
    public void run() {
        try {
            checkForUpdates();
        } catch (SocketException ex) {
            Logger.getLogger(RoutingTableUpdate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(RoutingTableUpdate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RoutingTableUpdate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
	 * This method checks if it's own routing table needs to be updated
     */
    public void checkForUpdates() throws SocketException, UnknownHostException, IOException {
///aw 3alock
        synchronized (this) {
            //       System.out.println("In check for updates method");

            boolean isUpdated = false;

            int destCost;
            //gets the port of which the router sent the RT  from.

            // Iterate through the neighbor's routing table
            Iterator<HashMap.Entry<RoutingTableKey, RoutingTableInfo>> routingEntriesIterator = recievedroutingtable.routingEntries.entrySet().iterator();

            while (routingEntriesIterator.hasNext()) {
                //fetching routing table as pairs
                HashMap.Entry<RoutingTableKey, RoutingTableInfo> pair = (HashMap.Entry<RoutingTableKey, RoutingTableInfo>) routingEntriesIterator.next();

                destAddress = (RoutingTableKey) pair.getKey();

                //"Checking if my routing table has an entry for "  destAddress.getHostAddress()
                if (rt.routingEntries.containsKey(destAddress)) {

                    destCost = rt.routingEntries.get(destAddress).cost;

                    //"Cost for this destination in my routing table is "  destCost
                    //"Cost for this destination in received routing table is "  pair.getValue().cost
                    if (destCost > (1 + pair.getValue().cost)) {
                        //which is smaller than my cost for the destination
                        Port p = rt.getPortClass(myport);
                        recievedport = rt.getNextHop(myport);
                        //nexthop
                        RoutingTableKey nextipHost = rt.getNextipHost(myport);

                        rt.updateEntry(destAddress.getIp(), destAddress.getHostname(), nextipHost, recievedport, p, 1 + pair.getValue().cost);
                        Platform.runLater(() -> {
                            VirtualRouter.buffer.appendText("Updating " + destAddress + " entry learned from " + destAddress + "\n");
                        });
                        isUpdated = true;

                    }

                } else//it does not contain it so add it 
                //iza next hop bdl row mano myport y3ne mano directly connected 3lye
                if (pair.getValue().nextHop != myport) {//hy krmel iza msh anaa krmell ma zid 7allle
                    Port p = rt.getPortClass(myport);
                    recievedport = rt.getNextHop(myport);
                    RoutingTableKey nextipHost = rt.getNextipHost(myport);
                    rt.addEntry(destAddress, nextipHost, recievedport, pair.getValue().cost + 1, myport, p, true, true);
                    isUpdated = true;
                    //System.out.println(nextipHost);
                    Platform.runLater(() -> {
                        VirtualRouter.buffer.appendText("Adding a new entry to the RT learned from " + destAddress + "\n");
                    });
                    System.out.println("*updated");
                }

                //If yes send updates to all neighbors
                if (isUpdated) {
                    System.out.print("\n");
                    rt.printTable("**Updated**");
                    System.out.print("\n");
                    for (HashMap.Entry<RoutingTableKey, RoutingTableInfo> entry : rt.routingEntries.entrySet()) {

                        if (entry.getValue().cost == 1) {
                            //new RoutingTableSend(oos, rt).start();
                            myPP.write(rt);
                            rt.printTable("**Sending** the updated RT to port " + entry.getValue().getNextHop() + " from port " + entry.getValue().getPort());
                        }
                    }

                }
            }

        }
    }
}
