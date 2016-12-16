/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Loic
 */
public class InternetConnection {
    
    /**
     * Check if there is an internet connection,
     * We check if we can ping google's DNS, certainly
     * always up ;)

     * @return  {@code true} if internet in enable,
     *          {@code false} if there is no internet connection
     */
    
    public static boolean isEnable() {
        boolean enable = false;
        
        try {
            InetAddress add = InetAddress.getByName("8.8.8.8");
            if(add.isReachable(500)){
                enable = true;
            }else{
                enable = false;
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(InternetConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InternetConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return enable;
    }
    
}
