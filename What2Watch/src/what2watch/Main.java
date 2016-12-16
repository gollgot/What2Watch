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
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
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
        
        Font test = Font.loadFont(getClass().getResourceAsStream("resources/fonts/SourceSansPro-Regular.otf"), 12);
        Font test2 = Font.loadFont(getClass().getResourceAsStream("resources/fonts/Montserrat-Bold.ttf"), 12);
        scene.getStylesheets().add("what2watch/default.css");
        
        // StageStyle.UNIFIED is for remove the basic blue border of the windows
        stage.initStyle(StageStyle.UNIFIED);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("What 2 Watch");
        stage.getIcons().add(new Image("what2watch/resources/images/W2W_Logo.png"));
        stage.show();
        
        // When we close the application, we assign true to the exit variable on the controler. 
        // This way, the controller class know we exit the programm, then, we can close all thread 
        // This case, the thread who check the internet connection. 
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() { 
            @Override 
            public void handle(WindowEvent event) { 
                // We close the programm 
                FXMLDocumentController.exit = true; 
            } 
        });
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
        ArrayList<String> originalListFiles = new ArrayList<>(); 
        
        // get path saved on the UserPreferences
        String path = userPref.getPath();
        
        try {
            fileBrowser.fetchMoviesFileNames(path);
            originalListFiles = fileBrowser.getMovieFileNames();
        } catch (IOException ex) {
            System.out.println("Error on Main : getFilesNames. Ex : "+ex);
        }
        ArrayList<String> finalListFiles = ParsingFiles.parse(originalListFiles);
        
        
        
        /* TEST IF MOVIE EXISTS OR NOT*/ 
        // it's an updating of cache
        /*for (int i = 0; i < finalListFiles.size(); i++) {
            System.out.println("Nom : "+finalListFiles.get(i));
        }*/
       //DbHandler dbHandler = new DbHandler(cacheDb,finalListFiles);
        
        
        
        
        
        /* After : Launch the window */
        launch(args);
    }
    
}
