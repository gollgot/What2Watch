package what2watch;

/*
 * The purpose of this class is to clean the name of on list of movies
 */

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 *
 * @author David.ROSAT - Loïc Dessaules
 */
public class ParsingFiles {
    
    /**
     * @param   name The name of the movie
     * 
     * @return  The name of the movie after exclusion of several terms.
     */
    public static String simplify_name(String name){
        // Add exclusion term on the REGEX
        String listExcludeExtension = "\\.mkv|\\.m4v|\\.mp4|\\.avi|\\.mpeg|\\.wmv|\\.flv|\\.mov";
        String listExcludeTypeExtension = "Mkv";
        String listExcludeLanguage = "FRENCH|VOSTA|VOSTFR|VF|FR|EN|TRUE|truefrench|Truefrench";
        String listExcludeTeam = "Ganesh-AC3|Dieudonne|Cpasbien|\\[www\\.Cpasbien\\.me\\]|GraNPa|byPhilou";
        String listExcludeFileResolution = "720p|1080p";        
        String listExcludeTypeRecord = "BluRay|DVDRIP|dvdrip|HD|3D|3d|Version|BDRip|Xvid|AC3|EDITION EXCLUSIVE|XviD";
        String listExcludeTypePonctuation = "\\.|-|;|,|_|\\(|\\)";
        String listExclude = listExcludeExtension +"|" + 
                             listExcludeTypeExtension + "|" +
                             listExcludeLanguage + "|" +
                             listExcludeTeam + "|" +
                             listExcludeFileResolution + "|" +
                             listExcludeTypeRecord  + "|" +
                             listExcludeTypePonctuation;
                          
        String result = name.replaceAll("("+listExclude+")"," ");  

        return result;
    }
    
    /**
     * @param   name The name of the movie
     * 
     * @return  The name of the movie after the exclusion of date founded
     */
    public static String removeNameDate(String name){
        String pattern = "(\\d{4})";
        String result = name.replaceAll(pattern, "");
              
        return result;
    }
    
    
    /**
     * When there is a double space, we keep the first bloc not empty
     * (so normaly the movie name)
     * 
     * @param   name The name of the movie
     * 
     * @return  The name of the movie after cleaning
     */
    public static String removeAfterDoubleSpace(String name){
    /*  We have double space generated by the "simplify_name" method.
        We split with the double space, so we have an array,
        
        Two possibly critical patterns : 
            1) at the beginning of the raw movie name (if there is one)
            2) If there is no pattern at the beginning of the raw movie name it is just after the raw movie name
        
        1 bis) now, if we have replaced with 2 spaces the first critical patern
        We keep the first bloc of the array not empty (this is the movie name)
        
        2 bis) If we have replaced with 2 spaces the 2nd critical patern,
        We keep the first bloc of the array not empty (this is the movie name)

        With this method, it's easy to remove all not term we don't have on the "simplify_name" method.
        but we have to found the critical patern.*/
    
        // array with all the bloc after regex traitment
        String[] tab = name.split("  ");
      
        int i=0;
        // We keep the first bloc not empty
        while(tab[i].equals("")  && i < tab.length){
            i++;
        }
        return tab[i];        
    }
    
    /**
     * @param   listfiles the list of the movie names you want to parse
     * 
     * @return  All movie names parsed
     */
    public static ArrayList<String> parse(ArrayList<String> listFiles)
    {

        ArrayList<String> results = new ArrayList<String>(); 
        for(int i = 0; i < listFiles.size(); i++)
        {
            String simplifiedName = simplify_name(listFiles.get(i));
            String name = removeNameDate(simplifiedName);
            name = removeAfterDoubleSpace(name);
            results.add(name);
        }
        return results;
    }
    
<<<<<<< HEAD
    /**
     * @param   videoFileName  the movie name you want to check
     * 
     * @return  True if the videoFileName is a TV Show; False otherwise
     */
=======
    
    /**
    * Checks whether a video file name contains patterns of a TV show.
    * 
    * This method performs a check for the following patterns:<br>
    * "s03e9" kind of pattern<br>
    * "1x05" kind of pattern<br>
    * "- 03 -" kind of pattern
    * 
    * @param videoFileName the String representing the video file name
    * 
    * @return  {@code true} if the file name contains a TV show pattern; 
    *          {@code false} otherwise 
    */
    public static boolean containsTVShowPattern(String videoFileName) {
        boolean containsPattern = false;
        
        // Detects "s03e9" kind of pattern
        Pattern classic = Pattern.compile("(([^a-zA-Z](s|S)[^a-zA-Z]?[0-9]{1,3})|(([^a-zA-Z](e|E)[^a-zA-Z]?[0-9]{1,3})))");
        // Detects "1x05" kind of pattern
        Pattern xed = Pattern.compile("([0-9]{1,2}[^a-zA-Z]?x[^a-zA-Z]?[0-9]{1,3})");
        // Detects "- 03 -" kind of pattern
        Pattern numbered = Pattern.compile("-[^a-zA-Z0-9]?[0-9]{1,4}[^a-zA-Z0-9]?-");
        
        containsPattern = classic.matcher(videoFileName).find() || xed.matcher(videoFileName).find() || numbered.matcher(videoFileName).find();
        
        return containsPattern;            
    }
}
