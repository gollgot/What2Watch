/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
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
     * We check if we can open a socket with google, certainly
     * always up ;)

     * @return  {@code true} if internet in enable,
     *          {@code false} if there is no internet connection
     */
    
    public static boolean isEnable() {
        boolean enable = false;
        
        try {
            Socket socket = new Socket("www.google.com", 80);
            if(socket.isConnected()){
                enable = true;
            }else{
                enable = false;
            }
        } catch (IOException ex) {
            enable = false;
        }
        
        return enable;
    }
    
}
