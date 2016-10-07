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
        
        /* TEST JSON */
        /*String filmTest = finalListFiles.get(20);
        filmTest = filmTest.replaceAll(" ", "%20");
        
        Movie movie = new Movie();
        // Fetch the JSON and add data into movie object
        System.out.println("http://www.omdbapi.com/?t="+filmTest+"&y=&plot=full&r=json");
        try {
            // Get a JSON from an URL
            // test
            JSONObject json = ParsingJSON.readJsonFromUrl("http://www.omdbapi.com/?t="+filmTest+"&y=&plot=full&r=json");
            // Set data on a movie object
            movie.setTitle(json.get("Title").toString());
            movie.setYear(json.get("Year").toString());
            movie.setDirector(json.get("Director").toString());
            movie.setActors(json.get("Actors").toString());
            movie.setGenre(json.get("Genre").toString());
            movie.setPoster(json.get("Poster").toString());
            movie.setSynopsis(json.get("Plot").toString());
       
        } catch (JSONException ex) {
            System.out.println("ERROR on parsingJSON (JSON exception) : "+ex.getMessage());
        } catch (IOException ex) {
            System.out.println("ERROR on parsingJSON (IO exception) : "+ex.getMessage() + "\nVeuillez vérifier votre connexion internet");
        }
        System.out.println("Title of the movie : "+movie.getTitle());
        System.out.println("Year : "+movie.getYear());
        System.out.println("S : "+movie.getSynopsis());
        */
        
        
        /* TEST IF MOVIE EXISTS OR NOT*/ 
        DbHandler dbHandler = new DbHandler(cacheDb,"Titanic");
        
        
        
        
        
        /* After : Launch the window */
        launch(args);
    }
    
}
