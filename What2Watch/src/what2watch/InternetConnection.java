/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Loic
 */
public class InternetConnection {
    
    /**
     * Check if there is an internet connection
     *
     *
     * @return  {@code true} if internet in enable,
     *          {@code false} if there is no internet connection
     */
    public static boolean isEnable(){
        boolean enable = false;  
        try 
        {
            URL url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();   
            enable = true;
        }catch (Exception e){
            // No internet connection    
        } 
        return enable;
    }
    
    
}
