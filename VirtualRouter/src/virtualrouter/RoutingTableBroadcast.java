/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrouter;

import java.io.IOException;
import sharedPackage.RoutingTableKey;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maria afara
 */
public class RoutingTableBroadcast extends Thread {

    // PortConxs portConxs;
    RoutingTable routingTable;
    ArrayList<InetAddress> networks;

    public RoutingTableBroadcast(RoutingTable routingTable) {

        this.routingTable = routingTable;
        // System.out.println("*in broadcast constructor");
    }

    /*
        *This method first establishes a socket connection with the dirclty connected network  (neighbors)
        * This method then sends routing table to all the neighbors every 60 or 30 sec.
     */
    @Override
    public void run() {

        //for the first time lzm tb3t request lal neighs ino yb3tula lrt 
        //l2elonn b3den btb3t le 2ela wbtsh8el ltimer
        while (true) {
            try {
                //       System.out.println("*in broadcast infinte loop");
                   HashMap<RoutingTableKey, RoutingTableInfo> temproutingEntries=routingTable.getRoutingEntries();

                for (HashMap.Entry<RoutingTableKey, RoutingTableInfo> entry : temproutingEntries.entrySet()) {
//fina bala condition lcost krml bel awal hne bs neighs bs iza static zydin shi afdl condion lcost krml hek
//isconnection established tnt2kad eno boolean lactivtaed mn jhten true eno ltnen 3mlin lconnecion msh bs wa7d menon
//l is established y3ne he lentry mn domn dekhlin bl routing protocol (le n3tet ka network bl configurations)
                    //     System.out.println("*in broadcast before  RoutingTableSend");
                    if (entry.getValue().cost == 1 && entry.getValue().portclass.isconnectionEstablished() && entry.getValue().isEstablished()) {
                    //    new RoutingTableSend(entry.getValue().portclass.getOos(), routingTable).start();
                        //      System.out.println("*in broadcast after RoutingTableSend ");
                      //  entry.getValue().portclass.getOos().reset();
                      entry.getValue().portclass.write(routingTable);
                     routingTable.printTable("Sending to port "+ entry.getValue().getNextHop() +"from port "+entry.getValue().getPort());
                       
                    }

                }
                //    System.out.println("socket in broadcast" + entry.getValue().getSocket());
                //  System.out.println("***********");

                //   System.out.println("*before sleep");
                // System.out.println("***********");
                long startTime = System.currentTimeMillis();

                /* ... the code being measured starts ... */
                // sleep for 5 seconds
                //  TimeUnit.SECONDS.sleep(5);
                Thread.sleep(10000);
                /* ... the code being measured ends ... */
                long endTime = System.currentTimeMillis();

                long timeElapsed = endTime - startTime;
                //  System.out.println("***********");

                //        System.out.println("*after sleep " + timeElapsed);
                //   System.out.println("***********");
            } catch (InterruptedException ex) {
                Logger.getLogger(RoutingTableBroadcast.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(RoutingTableBroadcast.class.getName()).log(Level.SEVERE, null, ex);
            } 

        }

    }

     public void stopBroadcast() {
        System.out.println("\n*stoped Broadcasting");
        this.stop();
    }

}
