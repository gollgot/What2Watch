/*
 * The purpose of this class is to work with the FileFinder class in the file tree walking process
 * and to serve as a platform to get files informations from
*/
package what2watch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Raphael.BAZZARI
 */
public class FileBrowser{
    private static String pattern = "*.{avi,mkv,mpeg,wmv,m4v,mp4,flv,mov}";
    private static UserPreferences prefs = new UserPreferences();
    private static FileFinder finder;

    // Custom methods
    
    /** 
     * Returns a list every movie file name 
     *  
     * @return  An array list of string containing every movie file name
     */
    public static ArrayList<String> getMovieFileNames() {
        return finder.getMovieFileNames();
    }
    
    /** 
     * Returns the path that matches the raw movie name 
     *  
     * @param   rawMovieName the raw movie file name for which the path will be fetched
     * 
     * @return  The path associated with the rawMovieName
     */
    public static String getFilePath(String rawMovieName) {
        return finder.findPathOf(rawMovieName);
    }
    
    /** 
     * Creates a FileFinder instance that will fetch movie file names and paths.
     * Those informations are to be fetched using methods provided in FileBrowser.
     * 
     * @see     FileBrowser#getMovieFileNames 
     * 
     * @see     FileBrowser#getFilePath 
     */
    public static void getMovieFileInfos() {
        try {
            String path = prefs.getPath();
            Path startingDir = Paths.get(path);
            finder = new FileFinder(startingDir, pattern);
            Files.walkFileTree(startingDir, finder);
        } catch (IOException ex) {
            Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
