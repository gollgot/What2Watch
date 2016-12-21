/*
 * This class is used for internet connection management
 */
package what2watch;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Loïc Dessaules
 */
public class InternetConnection {
    
    /**
     * Check if there is an internet connection,
     * We check if we can open a socket with google, certainly
     * always up ;)

     * @return  {@code true}    if internet in enable;
     *          {@code false}   if there is no internet connection
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
