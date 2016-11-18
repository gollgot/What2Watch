package what2watch;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 *
 * @author David.ROSAT - Loïc
 */
public class ParsingFiles {
    
    
    // Add exclusion term on the REGEX (It's for afine)
    public static String simplify_name(String name){
        String listExcludeExtension = "\\.mkv|\\.m4v|\\.mp4|\\.avi|\\.mpeg|\\.wmv|\\.flv|\\.mov";
        String listExcludeTypeExtension = "Mkv";
        String listExcludeLanguage = "FRENCH|VOSTA|VOSTFR|VF|FR|EN|TRUE|truefrench|Truefrench";
        String listExcludeTeam = "Ganesh-AC3|Dieudonne|Cpasbien|\\[www\\.Cpasbien\\.me\\]|GraNPa|byPhilou";
        String listExcludeFileResolution = "720p|1080p";        
        String listExcludeTypeRecord = "BluRay|DVDRIP|dvdrip|HD|3D|3d|Version|BDRip|Xvid|AC3|EDITION EXCLUSIVE|XviD";
        String listExcludeTypePonctuation = "\\.|-|;|,|_";
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
    
    
    public static ArrayList<String> parse(ArrayList<String> listFiles)
    {

        ArrayList<String> retour = new ArrayList<String>(); 
        for(int i = 0; i < listFiles.size(); i++)
        {
            //System.out.println("Oname : "+listFiles.get(i));
            String simplifiedName = simplify_name(listFiles.get(i));
            String name = removeNameDate(simplifiedName);
            name = removeAfterDoubleSpace(name);
            //System.out.println("name : "+name);
            retour.add(name);
        }
        return retour;
    }
    
    
    // Checks whether a video file name contains patterns of a TV show
    // Returns a boolean indicating if it does or not
    public static boolean containsTVShowPattern(String videoFileName) {
        boolean containsPattern = false;
        
        // Detects "s03e9" kind of pattern
        Pattern classic = Pattern.compile("(([^a-zA-Z](s|S)[^a-zA-Z]?[0-9]{1,3})|(([^a-zA-Z](e|E)[^a-zA-Z]?[0-9]{1,3})))");
        // Detects "1x05" kind of pattern
        Pattern xed = Pattern.compile("([0-9]{1,2}[^a-zA-Z]?x[^a-zA-Z]?[0-9]{1,3})");
        // Detects "- 03 -" kind of pattern
        Pattern numbered = Pattern.compile("-[^a-zA-Z0-9]?[0-9]{1,4}[^a-zA-Z0-9]?-");
        
        containsPattern = classic.matcher(videoFileName).find() || xed.matcher(videoFileName).find() || numbered.matcher(videoFileName).find();
        
        // for test purposes only
        // System.out.println(videoFileName + "\nis a show? " + containsPattern);
        
        return containsPattern;            
    }
}
