/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrouter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

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
    ArrayList<String> strings;

    public RoutingTableRecieve(Object recievedObject, int port, String hostname, ObjectInputStream ois, ObjectOutputStream oos, RoutingTable rt, Port myPp) {
        strings = new ArrayList<String>();
        System.out.println("routing table recieve initialized");

        Platform.runLater(() -> {
            VirtualRouter.buffer.appendText("routing table recieve initialized");
        });
        this.port = port;
        this.hostname = hostname;
        this.ois = ois;
        this.oos = oos;
        this.rt = rt;
        this.recievedObject = recievedObject;
        this.myPp = myPp;

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

                    myPp.write(rt);

                } catch (IOException ex) {
                    Logger.getLogger(RoutingTableRecieve.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            recieveport = rt.getNextHop(port);

           
            routingTable.printTable(" **Recieved** from " + recieveport + " at port " + port);
          

            // Check if this routing table's object needs to be updated
            new RoutingTableUpdate(routingTable, hostname, port, oos, rt, myPp).start();

        }

    }

    private RoutingTable recieveRoutingTable(Object recievedObject) {

        return routingTable = (RoutingTable) recievedObject;
    }

}
