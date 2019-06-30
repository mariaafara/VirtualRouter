/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrouter;

import sharedPackage.FailedNode;
import java.io.IOException;
import sharedPackage.RoutingTableKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maria afara
 */
public class FailedNodeRecieve extends Thread {

    FailedNode fn;//the one recieved
    Object recievedObject;
    private RoutingTable rt;
    RoutingTableKey myRTK;
    boolean canReceive;

    public FailedNodeRecieve(Object recievedObject, RoutingTable rt, RoutingTableKey myRTK, boolean canReceive) {

        this.rt = rt;
        this.recievedObject = recievedObject;
        this.myRTK = myRTK;
    }

    @Override
    public void run() {

        fn = recieveFailedNode(recievedObject);
        //    RoutingTableKey ipHost = new RoutingTableKey(fn.getInetaddress(), fn.getHostname());
        RoutingTableKey dest = fn.getDestination();
        RoutingTableKey nextipHost = fn.getMyKey();

        //hon bde 23ml delete lal entry le bel table 3nde le lkey le 2ela huwe l ip lal failed node
        //m3 w7ad n lportet le 2ela le huwe ha ykoun lnext hop bel nsbe ele m3 hay lnode
        ///!!!!!!!!!!!!!!!!!!!!!!!! commented newely
        ArrayList<FailedNode> arrayfn = rt.deleteFailedNodes(dest, nextipHost, myRTK);
        System.out.println("----------------" + arrayfn.size());
        if(arrayfn.size() > 0){
             new threadStopReceiveRT(canReceive, 15000).start();
        }
        for (HashMap.Entry<RoutingTableKey, RoutingTableInfo> entry2 : rt.routingEntries.entrySet()) {
            System.out.println("*now broadcasting");
            if (entry2.getValue().cost == 1) {
                try {
                    //lneighbors
                    for (int i = 0; i < arrayfn.size(); i++) {
                        System.out.print("\n*broadcast newfn to " + entry2.getKey());
                        entry2.getValue().portclass.getOos().writeObject(arrayfn.get(i));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(FailedNodeRecieve.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        System.out.print("\n");
        rt.printTable("After Deleting Failed Node  ");
        System.out.println("\n");

    }

    private FailedNode recieveFailedNode(Object recievedObject) {

        // get failed node object which is reccieved
        return fn = (FailedNode) recievedObject;
    }

}
