/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Loic
 */
public class InternetConnection {
    
    
    public static boolean isEnable(){
        boolean enable = false;  
        try {
            InetAddress add = InetAddress.getByName("8.8.8.8");
            if(add.isReachable(1000)){
                enable = true;
                System.out.println("Yes there is an internet connection !");
            }else{
                System.out.println("No internet connection !");
            }
        } catch (IOException ex) {
            Logger.getLogger(InternetConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return enable;
    }
    
    
}
