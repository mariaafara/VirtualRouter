/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrouter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maria afara
 */
public class RoutingTableRecieve extends Thread {

    int recieveport;

    ObjectInputStream ois;
    ObjectOutputStream oos;

    RoutingTable routingTable;//the one recieved

    private RoutingTable rt;
    private static int i = 0;
    private int port;
    private String hostname;
    Object recievedObject;
Port myPp;

    public RoutingTableRecieve(Object recievedObject, int port, String hostname, ObjectInputStream ois, ObjectOutputStream oos, RoutingTable rt, Port myPp) {

   //     System.out.println("routing table recieve initialized");
        this.port = port;
        this.hostname=hostname;
        this.ois = ois;
        this.oos = oos;
        this.rt = rt;
        this.recievedObject = recievedObject;
        this.myPp=myPp;

    }

    @Override
    public void run() {

        //recieve routing table
        routingTable = recieveRoutingTable(recievedObject);

        //if my routing table has been formed send the response
        if (!rt.isEmptyTable()) {
            if (i == 0) {
                i++;
                try {
                    //System.out.println("");
                    //   new RoutingTableSend(oos, rt).start();
                    myPp.write(rt);
//                try {
//                    oos.reset();
//                } catch (IOException ex) {
//                    Logger.getLogger(RoutingTableRecieve.class.getName()).log(Level.SEVERE, null, ex);
//                }
                } catch (IOException ex) {
                    Logger.getLogger(RoutingTableRecieve.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            recieveport = rt.getNextHop(port);

            System.out.print("\n");
            routingTable.printTable("Recieved from " + recieveport + " at port " + port);
            System.out.println("\n");

            // Check if this routing table's object needs to be updated
            new RoutingTableUpdate(routingTable,hostname, port,oos, rt, myPp).start();

        }

    }

    private RoutingTable recieveRoutingTable(Object recievedObject) {

        return routingTable = (RoutingTable) recievedObject;
    }

}
