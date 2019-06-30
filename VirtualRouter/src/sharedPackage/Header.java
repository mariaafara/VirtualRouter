/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharedPackage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

public class Header implements Serializable, Cloneable {
    
    String Version;
    long DHTL; //set it later, Length of the header
    String TypeOfService;
    int totalLength;  //length of the whole packet
    int Identification = -1;
    
    Boolean moreFragment;
    public int TTL; //TIME TO LIVE
    int Protocol; // the protocol of next level tcp or udp
    String headerCheksum;
    InetAddress sourceAddress;
    InetAddress destinationAddress;
    //int portSource;
    // int portDestination;
    String destinationHostname, sourceHostname;
    
    public Header(int Identification, int TTL, InetAddress sourceAddress, InetAddress destinationAddress, String sourceHostname, String destinationHostname) {//, int portSource, int portDestination
        this.Identification = Identification;
        Version = "4+";
        TypeOfService = "00000000";
        Protocol = 8;
        this.TTL = TTL;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
      //  this.portSource = portSource;
        this.destinationHostname = destinationHostname;
        this.sourceHostname = sourceHostname;
        //  this.portDestination = portDestination;

    }
    
    public RoutingTableKey getDestination() {
        return new RoutingTableKey(destinationAddress, destinationHostname);
    }
    
    public String getVersion() {
        return Version;
    }
    
    public long getDHTL() {
        return DHTL;
    }
    
    public String getTypeOfService() {
        return TypeOfService;
    }
    
    public int getTotalLength() {
        return totalLength;
    }
    
    public int getIdentification() {
        return Identification;
    }
    
    public String getDestinationHostname() {
        return destinationHostname;
    }
//
//    public int getPortDestination() {
//        return portDestination;
//    }

    public String getSourceHostname() {
        return sourceHostname;
    }
    
    public Boolean getMoreFragment() {
        return moreFragment;
    }
    
    public int getTTL() {
        return TTL;
    }
    
    public int getProtocol() {
        return Protocol;
    }
    
    public String getHeaderCheksum() {
        return headerCheksum;
    }
    
    public InetAddress getSourceAddress() {
        return sourceAddress;
    }
    
    public InetAddress getDestinationAddress() {
        return destinationAddress;
    }
//    
//    public int getPortSource() {
//        return portSource;
//    }

//    public int getPortDestination() {
//        return portDestination;
//    }
    public Header clone() throws CloneNotSupportedException {
        return (Header) super.clone();
    }
    
    @Override
    public String toString() {
        return "Header{" + "Version=" + Version + ", DHTL=" + DHTL + ", TypeOfService=" + TypeOfService + ", totalLength=" + totalLength + ", Identification=" + Identification + ", moreFragment=" + moreFragment + ", TTL=" + TTL + ", Protocol=" + Protocol + ", headerCheksum=" + headerCheksum + ", sourceAddress=" + sourceAddress + ", destinationAddress=" + destinationAddress + ", destinationHostname=" + destinationHostname + '}';
    }
    
    public String cheksumInput() {
        return "Header{" + " Identification=" + Identification + ", Version=" + Version + ", DHTL=" + DHTL + ", TypeOfService=" + TypeOfService + ", totalLength=" + totalLength + ", Protocol=" + Protocol + ", sourceAddress=" + sourceAddress + ", destinationAddress=" + destinationAddress +", hostnameDestination=" + destinationHostname + '}';
    }
    
    public static String getChecksum(Serializable object) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject("Das");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(baos.toByteArray());
            return DatatypeConverter.printHexBinary(thedigest);
        } finally {
            oos.close();
            baos.close();
        }
    }
}
