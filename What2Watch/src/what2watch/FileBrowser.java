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
    
    public static ArrayList<String> getMovieFileNames() {
        getMovieFileInfos();
        return finder.getMovieFileNames();
    }
    
    public static ArrayList<String> getMovieFilePaths() {
        getMovieFileInfos();
        return finder.getMovieFilePaths();
    }
    
    public static String getFilePath(String rawMovieName) {
        getMovieFileInfos();
        return finder.findPathOf(rawMovieName);
    }
    
    public static void getMovieFileInfos() {
        try {
            finder = new FileFinder();
            String path = prefs.getPath();
            Path startingDir = Paths.get(path);
            finder.setInitialDirectory(startingDir);
            finder.setPattern(pattern);
            Files.walkFileTree(startingDir, finder);
        } catch (IOException ex) {
            Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
