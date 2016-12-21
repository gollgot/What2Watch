/*
 * The purpose of this class is to store the data that might be useful for the user when he launches the program
 * e.g. The path leading to the folder containing movies
 */
package what2watch;

import java.util.prefs.Preferences;

/**
 *
 * @author Raphael.BAZZARI
 */
public class UserPreferences {
    private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

    public UserPreferences() {
    }
    
    /** 
     * Stores the movie folder path into the user's operating system.
     * The path is then be accessible from the app at any time.
     *  
     * @param   path the String representing the path of the folder containing
     * all of the user's movies
     */
    public void savePath(String path) {
        prefs.put("MovieFolderPath", path);
        System.out.println("The following path has been saved: " + path);
    }

    /** 
     * Returns the path representing the user's movie folder 
     * 
     * @return  The path of the user's movie folder
     */
    public String getPath() {
        return prefs.get("MovieFolderPath", "");
    }

    /** 
     * Removes the movie folder path from the user's operating system 
     */
    public void removePath() {
        prefs.remove("MovieFolderPath");
    }
}
