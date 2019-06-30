/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrouter;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import sharedPackage.RoutingTableKey;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

/**
 * this service sends routing table to all the directly connected neighbors
 *
 * @author maria afara
 */
public class RoutingTableSend extends Thread {

    private ObjectOutputStream oos;
    private RoutingTable rt;
   

    public RoutingTableSend(ObjectOutputStream oos, RoutingTable rt) {

        this.oos = oos;
        this.rt = rt;
       

    }

    /*
        * This method sends the routing table to all it's neighbors
     */
    @Override
    public void run() {

        // publish routing table here.
        //  System.out.println("*SendingRoutingTable");
        rt.printTable("Sending");
        //send myRoutingTable to neighbor
   //     sendRoutingTable(rt);
        
    }

    /*
        * This method sends given routing table 
     */
    private void sendRoutingTable(RoutingTable RT)  {

        try {

            //   oos.reset();
            //oos.writeObject(RT);
//What is going on here is that ObjectOutputStream detects that you are 
//        writing the same object every time. Each time theSet is written, 
//a "shared reference" to the object is written so that the same object is deserialized each time. 
//In this case you should use writeUnshared(Object) which will bypass this 
//mechanism, instead of writeObject(Object).
            oos.writeObject(RT);
            oos.flush();

            // oos.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
