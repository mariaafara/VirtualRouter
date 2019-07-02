package virtualrouter;

import sharedPackage.Neighbor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 * This class listens on given port for incoming connection
 *
 * @author maria afara
 */
public class PortConnectionWait extends Thread {

    ServerSocket serversocket;
    private Port p;
    Socket socket;
    String msg;
    int myport;

    RoutingTable rt;
    private Reciever reciever;
    private String myhostname;
 
    public PortConnectionWait(String myhostname, int myport, Port p, RoutingTable rt) {
    
        try {

            Platform.runLater(() -> {
                VirtualRouter.buffer.appendText("*port " + myport + " waiting for a conx");
            });
            System.out.println("*port " + myport + " waiting for a conx");
            serversocket = new ServerSocket(myport);
            this.p = p;
            this.myport = myport;
            this.myhostname = myhostname;
            this.rt = rt;

        } catch (IOException ex) {
            Logger.getLogger(PortConnectionWait.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {

        ObjectOutputStream objectOutputStream;
        while (true) {
            try {

                System.out.println("*port " + myport + " still waiting for a connection");

                socket = serversocket.accept();

                // rt.printTable("**Checking**");
                //  System.out.println("*socket :myport " + socket.getLocalPort() + " destport " + socket.getPort());
                System.out.println("*connection accepteed at port " + myport);

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Neighbor neighbor = (Neighbor) objectInputStream.readObject();

                //neighbor.neighborPort is the next hop 
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                if (rt.isExistandNotActive(neighbor.neighborAddress, neighbor.neighborname)) {

                    //  System.out.println("before activateEntry");
                    //  System.out.println("\n*neigh @ and port* " + neighbor.neighborAddress + " " + neighbor.neighborPort);
                    rt.activateEntry(neighbor.neighborAddress, neighbor.neighborname);

                    //  System.out.println("after activateEntry and before set socket");
                    // System.out.println("\n");
                    rt.printTable("--after add activation--");
                    // System.out.println("\n");

                    p.setSocket(socket);
                    p.setStreams(objectInputStream, objectOutputStream);
                    //  System.out.println("after setSocket");

                    p.setconnectionEstablished(true);

                    objectOutputStream.writeBoolean(true);
                    objectOutputStream.flush();

                    ///sar jehez yst2bel 
                    reciever = new Reciever(neighbor.getNeighborAddress(), neighbor.getNeighborname(), neighbor.getNeighborPort(), myport, myhostname, p.getOis(), p.getOos(), rt, p);
                    reciever.start();
                    p.setReciever(reciever);

                    System.out.println("*true was sent");

                } else {

                    objectOutputStream.writeBoolean(false);
                    objectOutputStream.flush();

                    System.out.println("*false was sent");
                    System.out.println("*my turn to establish the connection on my side with port " + myport);

                    socket.close();
                }

            } catch (IOException ex) {
                Logger.getLogger(PortConnectionWait.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PortConnectionWait.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void stopWaitingForConnection() {
        System.out.println("\n*stoped Waiting For Connection");
        this.stop();

    }
}
