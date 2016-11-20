/*
 * The purpose of this class is to handle the movie files used within the application
 * This class is based on https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/essential/io/examples/Find.java
 */
package what2watch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Raphael.BAZZARI
 */
public class FileBrowser{
    private PathMatcher matcher;
    private static String pattern = "*.{avi,mkv,mpeg,wmv,m4v,mp4,flv,mov}";
    private static ArrayList<String> movieFileNames = new ArrayList<String>();
    private static UserPreferences prefs = new UserPreferences();
    private static FileFinder finder = new FileFinder();

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
