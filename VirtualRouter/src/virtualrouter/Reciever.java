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
    ArrayList<String> strings;
    boolean canReceive = true;
//    public Reciever(InetAddress neighip, String myname, int myport, ObjectInputStream ois, ObjectOutputStream oos, RoutingTable rt) {
//
//        System.out.println("*reciever initialized");
//        this.port = myport;
//        this.ois = ois;
//        this.oos = oos;
//        this.rt = rt;
//        this.neighip = neighip;
//        ///router name aw ip ....n2sa 
//
//    }

    public Reciever(InetAddress neighip, String neighhostname, int neighport, int myport, String myhostname, ObjectInputStream ois, ObjectOutputStream oos, RoutingTable rt, Port myPortt) {
        strings = new ArrayList<String>();

//       strings.add("*reciever initialized");
//        VirtualRouter.printToScreen(strings);
//               strings.clear();
        Platform.runLater(() -> {
            VirtualRouter.buffer.appendText("*reciever initialized");
        });
        //VirtualRouter.buffer.appendText(System.getProperty("line.separator"));
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
                //System.out.println("*reciever* socket :myport " + socket.getLocalPort() + " destport " + socket.getPort());
                //     

                //hon oset lcnctions
                //iza packet jey mn netwrok 3nde ye w3mltlo estbalish bst2bla 
                //iza wslne msg wl src mno directly cnnected 3lye mb3ml shi b2lomfina nst2bla
                //  System.out.println("\n\n"+ois.available()+"\n\n");
                recievedObject = ois.readObject();
                i++;

                //  System.out.println("*recieved object =" + recievedObject);
                if (recievedObject instanceof RoutingTable) {
                    strings.add("*recieved routing table");
                    VirtualRouter.printToScreen(strings);
                    strings.clear();
                    // VirtualRouter.buffer.appendText(System.getProperty("line.separator"));
                    System.out.println("*recieved routing table");
                    if (canReceive) {
                        if (rt.isEstablishedEntry(neighip, neighhostname)) {

                            strings.add("entry established");
                            VirtualRouter.printToScreen(strings);
                            strings.clear();
                            //   VirtualRouter.buffer.appendText(System.getProperty("line.separator"));

                            new RoutingTableRecieve(recievedObject, myport, myhostname, ois, oos, rt, myPortt).start();

                        } else {
                            strings.add("Discarding routing table 1st else");
                            VirtualRouter.printToScreen(strings);
                            strings.clear();
                            // VirtualRouter.buffer.appendText(System.getProperty("line.separator"));
                            System.out.println("Discarding routing table");
                        }
                    } else {
                        strings.add("Discarding routing table 2nd else");
                        VirtualRouter.printToScreen(strings);
                        strings.clear();
                        //VirtualRouter.buffer.appendText(System.getProperty("line.separator"));
                        System.out.println("Discarding routing table");
                    }
                } else if (recievedObject instanceof FailedNode) {
                    //lzm nt2kad hon iza lzm lrouting protocol kmen bdo ykoun established awla 
                    strings.add("Recieved a failed node");
                    VirtualRouter.printToScreen(strings);
                    strings.clear();
                    // VirtualRouter.buffer.appendText(System.getProperty("line.separator"));
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
                                strings.add("Forwarding packet");
                                VirtualRouter.printToScreen(strings);
                                strings.clear();

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
            // stopRecieving();
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
        strings.add("Stoped Recieving at port " + myport);

        System.out.println("\n*stoped Recieving at port " + myport);
        this.stop();
    }
}
