/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrouter;

import sharedPackage.FailedNode;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import sharedPackage.Packet;
import sharedPackage.RoutingTableKey;

/**
 *
 * @author maria afara
 */
public class Reciever extends Thread {

    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;

    private RoutingTable rt;

    private int myport;

    private Object recievedObject;
    int neighport;
    InetAddress neighip;
    String neighhostname;
    String myhostname;
    Port myPortt;

    boolean canReceive = true;

    public Reciever(InetAddress neighip, String neighhostname, int neighport, int myport, String myhostname, ObjectInputStream ois, ObjectOutputStream oos, RoutingTable rt, Port myPortt) {

        Platform.runLater(() -> {
            VirtualRouter.buffer.appendText("*reciever initialized");
        });

        System.out.println("*reciever initialized");
        this.myport = myport;
        this.ois = ois;
        this.oos = oos;
        this.rt = rt;
        this.neighip = neighip;
        this.neighport = neighport;
        this.myhostname = myhostname;
        this.neighhostname = neighhostname;
        this.myPortt = myPortt;
    }

    @Override
    public void run() {

        try {
            //  portConxs.getPortInstance(port).wait();

            int i = 1;
            while (true) {

                System.out.println("*waiting to recieve object " + i + " from " + neighport);

                //hon oset lcnctions
                //iza packet jey mn netwrok 3nde ye w3mltlo estbalish bst2bla 
                //iza wslne msg wl src mno directly cnnected 3lye mb3ml shi b2lomfina nst2bla
                //  System.out.println("\n\n"+ois.available()+"\n\n");
                recievedObject = ois.readObject();
                i++;

                //  System.out.println("*recieved object =" + recievedObject);
                if (recievedObject instanceof RoutingTable) {

                    Platform.runLater(() -> {
                        VirtualRouter.buffer.appendText("*recieved routing table");
                    });
                    // VirtualRouter.buffer.appendText(System.getProperty("line.separator"));
                    System.out.println("*recieved routing table");
                    if (canReceive) {
                        if (rt.isEstablishedEntry(neighip, neighhostname)) {

                            new RoutingTableRecieve(recievedObject, myport, myhostname, ois, oos, rt, myPortt).start();

                        } else {

                            Platform.runLater(() -> {
                                VirtualRouter.buffer.appendText("*Discarding routing table 1st else");
                            });
                            // VirtualRouter.buffer.appendText(System.getProperty("line.separator"));
                            System.out.println("Discarding routing table");
                        }
                    } else {

                        Platform.runLater(() -> {
                            VirtualRouter.buffer.appendText("*Discarding routing table 2st else");
                        });
                        //VirtualRouter.buffer.appendText(System.getProperty("line.separator"));
                        System.out.println("Discarding routing table");
                    }
                } else if (recievedObject instanceof FailedNode) {
                    //lzm nt2kad hon iza lzm lrouting protocol kmen bdo ykoun established awla 

                    System.out.print("*recieved a failed node");
                    FailedNode fn = (FailedNode) recievedObject;
                    System.out.println("\n*" + fn.toString());
                    new FailedNodeRecieve(recievedObject, rt, new RoutingTableKey(InetAddress.getLocalHost(), myhostname), canReceive).start();

                } else if (recievedObject instanceof Packet) {
                    Packet p = (Packet) recievedObject;
                    String messageReceived;
                    if (p.header.getHeaderCheksum().equals(p.header.getChecksum(p.header.cheksumInput()))) {
                        System.out.println("*Cheksum verified");
                        int ttl = p.header.getTTL();
                        ttl--;
                        p.header.TTL = ttl;
                        if (ttl > 0) {
                            ///iza huwe zeto ana and and lhostname  !!!!!!
                            if (p.header.getDestinationAddress().equals(Inet4Address.getLocalHost().toString()) && p.header.getDestinationHostname().equals(myhostname)) {
                                messageReceived = p.Message;
                                System.out.println("*Received Message =" + messageReceived);
                                System.out.println("*From             =" + p.header.getSourceAddress() + ":" + p.header.getSourceHostname());

                            } else {

                                System.out.println("*forwarding packet");
                                ///b3tiha l ip wl host name  bdel get !!!!!
                                RoutingTableInfo rtInfo = rt.getEntry(p.header.getDestination());
                                if (rtInfo != null && rtInfo.activated == true && rtInfo.established == true) {
                                    ObjectOutputStream oos = rtInfo.portclass.oos;

                                    oos.writeObject(p);
                                } else {
                                    System.out.println("*Destination " + p.header.getDestination().toString() + " Doesnt exist Or Routing Didnt work yet..");
                                }

                            }

                        } else {
                            System.out.println("Packet TTL exceeded, therefore the message is dropped!");
                        }
                    } else {
                        System.out.println("*Cheksum not equal, there's an alteration of the message");
                        System.out.println("*Initial Cheksum =" + p.header.getHeaderCheksum());
                        System.out.println("*Current Cheksum =" + p.header.getChecksum(p.header.cheksumInput()));

                    }

                } else {
                    System.out.println("*recieved unknown type of object " + recievedObject.getClass());
                }

                Thread.sleep(2000);
            }
        } catch (IOException ex) {
            stopRecieving();
            Logger.getLogger(Reciever.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(" " + neighport);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Reciever.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(" " + neighport);
        } catch (InterruptedException ex) {
            Logger.getLogger(Reciever.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(" " + neighport);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Reciever.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(" " + neighport);
        }
    }

    public void stopRecieving() {

        Platform.runLater(() -> {
            VirtualRouter.buffer.appendText("Stoped Recieving at port " + myport);
        });
        System.out.println("\n*stoped Recieving at port " + myport);
        this.stop();
    }
}
