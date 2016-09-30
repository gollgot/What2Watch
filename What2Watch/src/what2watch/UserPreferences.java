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
    
    public void savePath(String path) {
        prefs.put("MovieFolderPath", path);
        System.out.println("The following path has been saved: " + path);
    }

    public String getPath() {
        return prefs.get("MovieFolderPath", "");
    }

    public void removePath() {
        prefs.remove("MovieFolderPath");
    }
}
