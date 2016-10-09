/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Raphael.BAZZARI
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /* First step : create the cache folder and file */
        CacheDb cacheDb = new CacheDb();
        // If DB file (for cache) doesn't exists, we will create one
        if(!cacheDb.exists()) {
            cacheDb.create();
        }else{
            System.out.println("The cache file already exists.");
        }
        
         
        /* TEST ParsingFiles */
        /*UserPreferences userPref = new UserPreferences();
        FileBrowser fileBrowser = new FileBrowser();
        ParsingFiles parsingFiles = new ParsingFiles();
        ArrayList<String> originalListFiles = new ArrayList<>(); 
        
        // get path saved on the UserPreferences
        String path = userPref.getPath();
        
        try {
            fileBrowser.fetchMoviesFileNames(path);
            originalListFiles = fileBrowser.getMovieFileNames();
        } catch (IOException ex) {
            System.out.println("Error on Main : getFilesNames. Ex : "+ex);
        }
        ArrayList<String> finalListFiles = parsingFiles.parse(originalListFiles);*/
        
        
        
        /* TEST IF MOVIE EXISTS OR NOT*/ 
        // it's an updating of cache
        //DbHandler dbHandler = new DbHandler(cacheDb,"king kong");
        
        
        
        
        
        /* After : Launch the window */
        launch(args);
    }
    
}
