package what2watch;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;

/**
 *
 * @author David.ROSAT - Loïc
 */
public class ParsingFiles {
    
    
    // Add exclusion term on the REGEX (It's for afine)
    public static String simplify_name(String name){
        String listExcludeExtension = ".mkv|.avi|.m4v";
        String listExcludeTypeExtension = "Mkv";
        String listExcludeLanguage = "FRENCH|VOSTA|VOSTFR|VF";
        String listExcludeTeam = "Ganesh-AC3";
        String listExcludeFileResolution = "720p|1080p";        
        String listExcludeTypeRecord = "BluRay|DVDRIP|3D|3d";
        String listExcludeTypePonctuation = "\\.|-|;|,";
        String listExclude = listExcludeExtension +"|" + 
                             listExcludeTypeExtension + "|" +
                             listExcludeLanguage + "|" +
                             listExcludeTeam + "|" +
                             listExcludeFileResolution + "|" +
                             listExcludeTypeRecord  + "|" +
                             listExcludeTypePonctuation;
                          
        String result = name.replaceAll("("+listExclude+")"," ");  
        
        // Now create matcher object.
        return result;
    }
    
    
    // Remove date on the original file name
    public static String removeNameDate(String name){

        String pattern = "(\\d{4})";
        String result = name.replaceAll(pattern, "");
              
        return result;
    }
    
    
    // When there is a double space, we keep the first bloc not empty
    // (so, the first not empty bloc is normally the name of the movie)
    // With this method, it's easy to remove all not term wanted like : ".AC3-BROTHERS" , "Xvid-Destroy"
    public static String removeAfterDoubleSpace(String name){
        
        // array with all the bloc after regex traitment
        String[] tab = name.split("  ");
      
        int i=0;
        // We keep the first bloc not empty
        while(tab[i].equals("")  && i < tab.length)
        {
            i++;
        }
        return tab[i];        
    }
    
    
    public ArrayList<String> parse(ArrayList<String> listFiles)
    {

        ArrayList<String> retour = new ArrayList<String>(); 
        for(int i = 0; i < listFiles.size(); i++)
        {
            System.out.println("Oname : "+listFiles.get(i));
            String simplifiedName = simplify_name(listFiles.get(i));
            String name = removeNameDate(simplifiedName);
            name = removeAfterDoubleSpace(name);
            System.out.println("name : "+name);
            retour.add(name);
        }
        return retour;
    }
}
