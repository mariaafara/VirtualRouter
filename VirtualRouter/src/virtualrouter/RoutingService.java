package virtualrouter;

import sharedPackage.RoutingTableKey;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * there is no need t use this thread anymore
 *
 * @author maria afara
 */
public class RoutingService extends Thread {

    RoutingTable routingTable;
    ArrayList<RoutingTableKey> networks;
    public RoutingTableBroadcast routingTableBroadcast;

    public RoutingService(RoutingTable routingTable, ArrayList< RoutingTableKey> networks) {

        this.networks = networks;

        this.routingTable = routingTable;

    }

    /*
        *This method start broadcasting the router's routing table 
     */
    @Override
    public void run() {

        super.run();
        //establishing the routing protocol for  the networks 
        //assigned i.e allowing broadcasting and recieving routing table from only those networks
        //ktir important he shi 
        ///Hon ma lezm n3ml establish la routing protocol la entry mana activated 
        for (int i = 0; i < networks.size(); i++) {
            // System.out.println("networks looop\n");

            if (routingTable.routingEntries.get(networks.get(i)).activated) {
                routingTable.establishEntry(networks.get(i));
            }
        }

        //sar kel port 3ndo sender w reciever
        //allow to recieve objects at each port
//        for (HashMap.Entry<String, RoutingTableInfo> entry : routingTable.routingEntries.entrySet()) {
//            System.out.println("*in RoutingService in hashmap loop ");
//            //fina bala lcondition kermel bel awal bs neighbors mwjudin bl table
//            if (entry.getValue().cost == 1) {
//
//                System.out.println("*socket in reciever local= " + entry.getValue().portclass.getSocket().getLocalPort() + " port=" + entry.getValue().portclass.getSocket().getPort());
//
//                System.out.println("*run reciever for the port " + entry.getKey());
//
//                new Reciever(entry.getKey(), entry.getValue().port, entry.getValue().portclass.getOis(), entry.getValue().portclass.getOos(), routingTable).start();
//            }
//        }
        routingTableBroadcast = new RoutingTableBroadcast(routingTable);
        routingTableBroadcast.start();
        System.out.println("\n*start broadcast");
    }

  
}
