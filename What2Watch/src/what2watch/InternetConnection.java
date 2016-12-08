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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Loic
 */
public class InternetConnection {
    
    
    public static boolean isEnable(){
        boolean enable = false;  
        try 
        {
            URL url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();   
            enable = true;
        }catch (Exception e){
            System.out.println("Error on isEnable() in InternetConnection class. Ex: "+e.getMessage().toString());     
        } 
        return enable;
    }
    
    
}
