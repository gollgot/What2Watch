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
        // This variable holds the magic numbers (file signatures)
    // of each file types specified in the pattern field above
    private static  String[] fileSignatures = {
        "52494646", "41564920", "4C495354", "1A45DFA3",
        "93428288", "6D617472", "6F736B61", "3026B275",
        "8E66CF11", "A6D900AA", "0062CE6C", "00000018",
        "66747970", "6D703432", "00000014", "69736F6D",
        "33677035", "0000001C", "4D534E56", "01290046",
        "464C5601", "71742020", "6D6F6F76", "66726565",
        "6D646174", "77696465", "706E6F74", "736B6970",
        "00000020", "000018F7"
    };

    // Custom methods
    
    public static ArrayList<String> getMovieFileNames() {
        return finder.getMovieFileNames();
    }
    
    public static String getFilePath(String rawMovieName) {
        return finder.findPathOf(rawMovieName);
    }
    
    public static void getMovieFileInfos() {
        try {
            String path = prefs.getPath();
            Path startingDir = Paths.get(path);
            finder = new FileFinder(startingDir, pattern, fileSignatures);
            Files.walkFileTree(startingDir, finder);
        } catch (IOException ex) {
            Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
