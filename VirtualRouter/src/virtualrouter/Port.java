/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrouter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author maria afara
 */
public class Port extends Thread {

    boolean connectionEstablished;
    Socket socket;
    Reciever reciever;
    int myport;
    PortConnectionWait portConnectionWait;
    PortConnectionEstablish portConnectionEstablish;

    final Object lockconnectionEstablished = new Object();
    final Object lockSocket = new Object();
    final Object lockOos = new Object();
    final Object lockOis = new Object();

    RoutingTable rt;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    String myhostname;

    public Port(int myport, String myhostname, RoutingTable rt) {

        System.out.println("*Port " + myport + " initialized");

        this.connectionEstablished = false;
        this.myport = myport;
        this.socket = null;
        this.rt = rt;
        portConnectionWait = new PortConnectionWait(myhostname, myport, this, rt);
        this.ois = null;
        this.oos = null;
        this.myhostname = myhostname;
    }

    public Reciever getRecievers() {
        synchronized (lockconnectionEstablished) {
            return reciever;
        }
    }

    public void setReciever(Reciever reciever) {
        synchronized (lockconnectionEstablished) {
            this.reciever = reciever;
        }
    }

    public boolean isconnectionEstablished() {
        synchronized (lockconnectionEstablished) {
            return connectionEstablished;
        }
    }

    public void setconnectionEstablished(boolean connectionEstablished) {
        synchronized (lockconnectionEstablished) {
            this.connectionEstablished = connectionEstablished;
//            if (connectionEstablished) {
//                this.notifyAll();
//            }

        }
    }

    public Socket getSocket() {
        // System.out.println("*in getScoket method1");
        // System.out.println("*socket in reciever local= " + socket.getLocalPort() + " port=" + socket.getPort());

        synchronized (lockSocket) {
            //    System.out.println("*in getScoket method2");
            return socket;
        }
    }

    public void setSocket(Socket socket) throws IOException {
        //      System.out.println("in setScoket method2 for port " + myport + " before snchronized");

        synchronized (lockSocket) {
            this.socket = socket;
            //  System.out.println("*2*in setScoket method2 for port " + myport + " after ");

            //System.out.println("*2*in setScoket method2 for port " + myport + " after snchronized after streams");
        }
    }

    public void setStreams(ObjectInputStream ois, ObjectOutputStream oos) {
        setOis(ois);
        setOos(oos);
    }

    public ObjectInputStream getOis() {
        synchronized (lockOos) {
            return ois;
        }
    }

    public void setOis(ObjectInputStream ois) {
        synchronized (lockOis) {
            this.ois = ois;
        }
    }

    public ObjectOutputStream getOos() {
        synchronized (lockOos) {
            return oos;
        }
    }

    public void setOos(ObjectOutputStream oos) {
        synchronized (lockOis) {
            this.oos = oos;
        }
    }

    public void write(Object o) throws IOException {
        synchronized (lockOos) {
            oos.writeObject(o);
            oos.reset();
            oos.flush();
        }
    }

    @Override
    public void run() {

        super.run();
        portConnectionWait.start();

    }

//    public void connect(int port, InetAddress neighborAddress, int neighborport) {
//
//        PortConnectionEstablish pce = new PortConnectionEstablish(port, neighborAddress, neighborport, this, rt);
//        pce.start();
//    }
    public void connect(InetAddress neighborAddress, String neighborhostname, int neighborport) {

        PortConnectionEstablish pce = new PortConnectionEstablish(myport, myhostname, neighborAddress, neighborhostname, neighborport, this, rt
        );
        pce.start();
    }
}
