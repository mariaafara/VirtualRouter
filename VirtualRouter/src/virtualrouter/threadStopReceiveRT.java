/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrouter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AliFakih
 */
public class threadStopReceiveRT extends Thread{
     boolean canReceive;
     int sleep;
    public threadStopReceiveRT( boolean canReceive, int sleep){
        this.canReceive=canReceive;
        this.sleep=sleep;
    }
    public void run(){
         try {
             System.out.println("CANT RECEIVE ROUTING TABLE NOW");
             canReceive=false;
             Thread.sleep(sleep);
             canReceive=true;
             System.out.println("NOW WE CAN RECEIVE ROUTING TABLE");
         } catch (InterruptedException ex) {
             Logger.getLogger(threadStopReceiveRT.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    
}
