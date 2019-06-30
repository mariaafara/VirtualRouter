/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharedPackage;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class Packet implements Serializable{

    public Header header;
    public String Message;

    public Packet(Header header, String Message) {
        this.header = header;
        this.Message = Message;
    }

}
