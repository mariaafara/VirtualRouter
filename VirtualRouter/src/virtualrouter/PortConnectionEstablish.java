package virtualrouter;

import sharedPackage.Neighbor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import sharedPackage.RoutingTableKey;

/**
 *
 * @author maria afara
 */
public class PortConnectionEstablish extends Thread {

    private Port p;
    private Socket socket;
    private int neighborport;
    private InetAddress neighborip;

    private int myport;

    private RoutingTable rt;

    private String neighborhostname;
    private String myhostname;
    private Reciever reciever;
    ArrayList<String> strings;

    public PortConnectionEstablish(int myport, String myhostname, InetAddress neighborip, String neighborhostname, int neighborport, Port p, RoutingTable rt) {
        strings = new ArrayList<String>();
        this.neighborport = neighborport;
        this.myport = myport;
        this.p = p;
        this.neighborip = neighborip;
        this.rt = rt;
        this.neighborhostname = neighborhostname;
        this.myhostname = myhostname;
    }

    @Override
    public void run() {

        boolean bool;
        if (!p.isconnectionEstablished()) {

            try {

                Platform.runLater(() -> {
                    VirtualRouter.buffer.appendText("*establishing connection with ip=" + neighborip + " port=" + neighborport);
                });
                System.out.println("*establishing connection with ip=" + neighborip + " port=" + neighborport);
                //     socket = new Socket();
                //  socket.connect(new InetSocketAddress(neighborip, neighborport),1000);
                socket = new Socket(neighborip, neighborport);

                //System.out.println("*socket : myport " + socket.getLocalPort() + " destport " + socket.getPort());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                objectOutputStream.writeObject(new Neighbor(Router.ipAddress, myhostname, myport));

                //System.out.println("*sending my self as a neighbor to ip=" + InetAddress.getLocalHost() + " port=" + myport);
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                bool = objectInputStream.readBoolean();

                System.out.println("*" + bool + " was recieved");

                if (bool) {

                    p.setSocket(socket);
                    p.setStreams(objectInputStream, objectOutputStream);

                    p.setconnectionEstablished(true);

                    System.out.println("*connection is established at port " + myport + " with neighb = " + neighborhostname + " , " + neighborport);
                    rt.addEntry(neighborip, neighborhostname, new RoutingTableKey(neighborip, neighborhostname), neighborport, 1, myport, p, true, false);

                    reciever = new Reciever(neighborip, neighborhostname, neighborport, myport, myhostname, p.getOis(), p.getOos(), rt, p);
                    reciever.start();
                    p.setReciever(reciever);

                    //   System.out.println("\n*after initialize reciever at port " + myport + "\n");
                    //   System.out.println("\n*neig name " + neighborip + "," + neighborport + "\n");
                    rt.printTable("--after add true--");
                } else {

                    rt.addEntry(neighborip, neighborhostname, new RoutingTableKey(neighborip, neighborhostname), neighborport, 1, myport, p, false, false);

                    System.out.println("*waiting a connection from " + neighborport);
                    //  rt.printTable("**Checking**");

                    rt.printTable("--after add false--");
                    socket.close();
                }

            } catch (IOException ex) {
                Logger.getLogger(PortConnectionEstablish.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            //already exist a conx at this port
            //and i have to implement a methode to delete the old conx
            System.out.println("*you have to delete the old connection");
        }
    }
}
