/*
 *  The purpose of this class is to manage all datas / functions in like to
 *  the API.
 */
package what2watch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Loïc Dessaules
 */
public class ApiHandler {
    private static String apiKey = getApiKey();
    
    /** 
     * Return a Movie object contains all datas of the movieName passed
     * in parameter.
     * 
     * @param   movieName The name of the movie, after passed to the 
     *          ParsingFiles class (need for ask the API).
     * 
     * @param   rawMovieName The raw name of the movie.
     * 
     * @param   oneStepPourcent The step value (between 0 and 1) to add
     *          when one movie finished to fetch data from the API.
     * 
     * @param   progressBarProcess The ProgressBar object, for update it.
     * 
     * @return  A Movie object contains all movie's data.
     * 
     * @see     InternetConnection#isEnable
     * @see     ApiHandler#getMovieId
     * @see     ApiHandler#getMovieDetails
     * @see     ApiHandler#getMovieActorsDirectors
     * @see     ApiHandler#updateProgressBar
     */
    public static Movie getAllMovieInfos(String movieName, String rawMovieName, float oneStepPourcent, ProgressBar progressBarProcess){
        // We have 3 big process, so 33% of the oneStepPourcent.
        float pourcentToAdd = 33 * oneStepPourcent / 100;
            
        Movie movie = new Movie();
        try {
            if(InternetConnection.isEnable()){ 
                //between each request, we wait 180ms, because we have a limit with the API...
                String id = getMovieId(movieName); 
                updateProgressBar(progressBarProcess, pourcentToAdd);
                Thread.sleep(200);           
                
                movie = getMovieDetails(movieName, rawMovieName, id); 
                updateProgressBar(progressBarProcess, pourcentToAdd);
                Thread.sleep(200); 
 
                movie = getMovieActorsDirectors(movie, id); 
                updateProgressBar(progressBarProcess, pourcentToAdd);    
            }else{ 
                movie.setActors("Unknown"); 
                movie.setDirector("Unknown"); 
                movie.setGenre("Unknown"); 
                movie.setPoster("Unknown"); 
                movie.setRawTitle(rawMovieName); 
                movie.setSynopsis("Unknown"); 
                movie.setTitle(movieName); 
                movie.setYear("Unknown"); 
            } 
        } catch (InterruptedException ex) {
            Logger.getLogger(ApiHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return movie;
    }
    
    /** 
     * Update the progressBar passed in parameter of ApiHandler.getAllMovieInfos().
     * We have to do that in the JavaFX Main Thread, because we have to separate 
     * logical from graphical things.
     * 
     * @param   progressBarProcess The ProgressBar object you want to update.
     * 
     * @param   pourcentToAdd pourcent you want to add to the current progress value.
     * 
     * @see     ApiHandler#getAllMovieInfos
     * @see     Platform#runLater
     * @see     ProgressBar#setProgress
     */
    private static void updateProgressBar(ProgressBar progressBarProcess, float pourcentToAdd){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                progressBarProcess.setProgress(progressBarProcess.getProgress()+pourcentToAdd);
            }
        });
    }
    
    /** 
     * Return the ID of the movie stocked in "The Movie DataBase API".
     * (Used JSON)
     * 
     * @param   movieName The movie name you want to fetch the ID.
     * 
     * @see     ApiHandler#getAllMovieInfos   
     * @see     Platform#runLater
     */
    private static String getMovieId(String movieName) {
        String movieNameUrlFormat = movieName.replaceAll(" ", "%20");
        String id = "Unknown";

        try {
            // Get a JSON from an URL
            JSONObject json = ParsingJSON.readJsonFromUrl("https://api.themoviedb.org/3/search/movie?api_key="+ apiKey + "&language=en-US&query=" + movieNameUrlFormat);
            JSONArray jsonArray = json.getJSONArray("results");
            
            // If there is no infos for the film
            if(jsonArray.isNull(0)){
                id = "Unknown";
            }else{
                // Get the first object of "results array"
                JSONObject jsonOject = jsonArray.getJSONObject(0);
                id = String.valueOf(jsonOject.getInt("id"));
            }
        } catch (JSONException ex) {
            System.out.println("ERROR on parsingJSON (JSON exception) : " + ex.getMessage());
        } catch (IOException ex) { // Possibly InternetConnection lost 
            System.out.println("ERROR on parsingJSON (IO exception) : " + ex.getMessage() + "\nVeuillez vérifier votre connexion internet");
            id = "Unknown"; 
        }
        
        return id;
        
    }
    
    
    /** 
     * Return a Movie object who contains basics movie's data :
     * title, year, synopsis, poster_link, genres.
     * 
     * (fetch data in the JSON file from the TMDB API)
     * 
     * @param   movieName The movie name you want to fetch data.
     * 
     * @param   rawMovieName The raw movie name, you need it because we need to set this data
     *          to the Movie object.
     * 
     * @param   id The TMDB API's ID of the movie you want to fetch datas.
     * 
     * @return  Movie object
     */
    private static Movie getMovieDetails(String movieName, String rawMovieName, String id){
        String originalTitle = movieName;
        String year = "Unknown";
        String synopsis = "Unknown";
        String poster_link = "Unknown";
        String genres = "";
        
        // If we have an id, we get the real data, else, we don't have data so we put "unknown"
        if(id != "Unknown"){
            try {
                // Get a JSON from an URL
                JSONObject jsonObject = ParsingJSON.readJsonFromUrl("https://api.themoviedb.org/3/movie/" + id + "?api_key="+apiKey+"&language=en-US");
                
                /* TITLE */
                if(jsonObject.getString("title").equals("")){
                    originalTitle = "Unknown";
                }else{
                    originalTitle = jsonObject.getString("title");
                }
                
                /* SYNOPSIS */
                if(jsonObject.isNull("overview") || jsonObject.getString("overview").equals("")){ // .isNull first else ther is an error
                    synopsis = "Unknown";
                }else{
                    synopsis = jsonObject.getString("overview");
                    // Replace " with ` because we can have an error later with query on DB
                    synopsis = synopsis.replaceAll("\\\"", "`");
                }
                
                /* YEAR */
                if(jsonObject.getString("release_date").equals("")){
                    year = "Unknown";
                }else{
                    year = jsonObject.getString("release_date");
                    year = year.substring(0, 4);
                }
                
                /* POSTER PATH*/
                if(jsonObject.getString("poster_path").equals("")){
                    poster_link = "";
                }else{
                    poster_link = jsonObject.getString("poster_path");
                }
                
                /* GENRES */
                JSONArray jsonArrayGenres = jsonObject.getJSONArray("genres");
                if(jsonArrayGenres.isNull(0)){
                    genres += "Unknown";
                }else{
                    for (int i = 0; i < jsonArrayGenres.length(); i++) {
                        JSONObject jsonObjectGenres = jsonArrayGenres.getJSONObject(i);
                        // If this is the last genres listed (i < total-1), we display just the name
                        if(i == jsonArrayGenres.length()-1){
                            genres += jsonObjectGenres.getString("name");
                        }
                        // else, we display the name + ";"
                        else{
                            genres += jsonObjectGenres.getString("name")+";";
                        }
                        // Like that we have : "Fantasy;Action;Comedy"
                    }
                }
            } catch (JSONException ex) {
                System.out.println("ERROR on parsingJSON (JSON exception) : " + ex.getMessage());
            } catch (IOException ex) { // Internet connection lost 
                System.out.println("ERROR on parsingJSON (IO exception) : " + ex.getMessage() + "\nVeuillez vérifier votre connexion internet");       
                year = "Unknown"; 
                synopsis = "Unknown"; 
                poster_link = "Unknown"; 
                genres = "Unknown"; 
            }

        }
        // If id == Unknown, we add Unknown on the genres
        else{
            genres = "Unknown";
        }
        
        // Create the movie Object (without actors and director, we do that in the next method)
        Movie movie = new Movie();
        movie.setTitle(originalTitle);
        movie.setRawTitle(rawMovieName);
        movie.setYear(year);
        movie.setPoster(poster_link);
        movie.setSynopsis(synopsis);
        movie.setGenre(genres);
        
        return movie;
    }
    
    
    /** 
     * Return a Movie object who contains all movie's data we needs
     * 
     * (fetch Actors and Directors data in the JSON file from the TMDB API)
     * 
     * @param   movie The current movie that we add data on it.
     * 
     * @param   id The TMDB API's ID of the movie you want to fetch datas.
     * 
     * @return  Movie object
     */
    private static Movie getMovieActorsDirectors(Movie movie, String id){
       String actors = "";
       String directors = "";
       
       if(id != "Unknown"){
            try {
                // Get a JSON from an URL
                JSONObject json = ParsingJSON.readJsonFromUrl("https://api.themoviedb.org/3/movie/"+id+"/credits?api_key="+apiKey);

                /* CASTING */
                JSONArray jsonArrayCast = json.getJSONArray("cast");      
                if(jsonArrayCast.isNull(0)){
                    actors = "Unknown";
                }else{
                    for (int i = 0; i < jsonArrayCast.length(); i++) {
                        JSONObject jsonObject = jsonArrayCast.getJSONObject(i);
                        // If this is the last actor listed (i < total-1), we display just the name, else a ;
                        if(i == jsonArrayCast.length()-1){
                            actors += jsonObject.getString("name");
                        }
                        else{
                            actors += jsonObject.getString("name")+";";
                        }
                        // Like that we have : "actor1;Actor2;Actor3"
                    }
                }

                /* CREW */
                JSONArray jsonArrayCrew = json.getJSONArray("crew");      
                if(jsonArrayCrew.isNull(0)){
                    directors = "Unknown";
                }else{
                    // We get all peoples
                    for (int i = 0; i < jsonArrayCrew.length(); i++) {
                        JSONObject jsonObject = jsonArrayCrew.getJSONObject(i);
                        // If the job of the person is Director
                        if(jsonObject.getString("job").equals("Director")){
                            directors += jsonObject.getString("name")+";";
                        }
                    }
                    // If all the people don't have de Director job :
                    if(directors == ""){
                        directors = "Unknown";
                    }else{
                        // We delete the last char of the String (last ";") to have : director1;director2;director3
                        directors = directors.substring(0, directors.length()-1);
                    }
                }
            } catch (JSONException ex) {
                System.out.println("ERROR on parsingJSON (JSON exception) : " + ex.getMessage());
            } catch (IOException ex) { // Internet connection lost 
                System.out.println("ERROR on parsingJSON (IO exception) : " + ex.getMessage() + "\nVeuillez vérifier votre connexion internet");
                actors = "Unknown"; 
                directors = "Unknown"; 
            }
       }
       // If id == Unknown
       else{
           actors = "Unknown";
           directors = "Unknown";
       }
        
        movie.setActors(actors);
        movie.setDirector(directors);
        
        return movie;
    }
    
    /** 
     * Return the API KEY of TMDB API founded in a out of the project's sources file (.env).
     * (We put the key on an external file. This way, we don't have our key on the repo.)
     * 
     * @return  The API KEY
     */
    private static String getApiKey(){
        String envPath = ".env";
        String content = "";
        String[] contents = null;
        String apiKey = "";

         try {
             Scanner fileIn = new Scanner(new File(envPath));
             while(fileIn.hasNextLine()){
                 content += fileIn.nextLine();
             }
         } catch (FileNotFoundException ex) {
             System.out.println("Error on ApiHandler.getApiKey() Ex: "+ex.getMessage().toString());
         }

         contents = content.split(":");
         for(int i = 0; i < contents.length; i++){
             if(contents[i].equals("api_key")){
                 apiKey = contents[i+1];
             }
         }

         return apiKey;
    }
    
    
}
