/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ProgressIndicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author loic.dessaules
 */
public class ApiHandler {
    
    private static String apiKey = "9a52628ae3939c738592ac50fdd73f7c";

    // We get all movie datas, and between each request, we wait 180ms, because we have a limit with the API...
    public static Movie getAllMovieInfos(String movieName, String rawMovieName, float oneStepPourcent, ProgressIndicator progressIndicator){
        
        
        // We have 3 big process, so 33% of the oneStepPourcent.
        float pourcentToAdd = 33 * oneStepPourcent / 100;
            
        Movie movie = new Movie();
        try {
            // Each time, we add this pourcentToAdd to the current progressNumber
            // Beacause we add all the time the same value to each step
            
            if(InternetConnection.isEnable()){ 
                String id = getMovieId(movieName); 
                progressIndicator.setProgress(progressIndicator.getProgress()+pourcentToAdd); 
                Thread.sleep(200); 
 
                movie = getMovieDetails(movieName, rawMovieName, id); 
                progressIndicator.setProgress(progressIndicator.getProgress()+pourcentToAdd); 
                Thread.sleep(200); 
 
                movie = getMovieActorsDirectors(movie, id); 
                progressIndicator.setProgress(progressIndicator.getProgress()+pourcentToAdd); 
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
    
    
    private static String getMovieId(String movieName) {
        String movieNameUrlFormat = movieName.replaceAll(" ", "%20");
        String id = "Unknown";

        // Fetch the JSON and add data into movie object
        try {
            // Get a JSON from an URL
            JSONObject json = ParsingJSON.readJsonFromUrl("https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&language=en-US&query=" + movieNameUrlFormat);
            // Get the array data "results"
            JSONArray jsonArray = json.getJSONArray("results");
            
            // If there is no infos for the film
            if(jsonArray.isNull(0)){
                id = "Unknown";
            }else{
                // Get the first object of "results array"
                JSONObject jsonOject = jsonArray.getJSONObject(0);
                id = String.valueOf(jsonOject.getInt("id"));
            }
        
        // Differents error (JSON / IO)
        } catch (JSONException ex) {
            System.out.println("ERROR on parsingJSON (JSON exception) : " + ex.getMessage());
        } catch (IOException ex) { // InternetConnection lost 
            System.out.println("ERROR on parsingJSON (IO exception) : " + ex.getMessage() + "\nVeuillez vérifier votre connexion internet");
            id = "Unknown"; 
        }
        
        return id;
        
    }
    
    private static Movie getMovieDetails(String movieName, String rawMovieName, String id){
        String originalTitle = movieName;
        String year = "Unknown";
        String synopsis = "Unknown";
        String poster_link = "Unknown";
        String genres = "";
        
        // If we have an id, we get the real data, else, we don't have data so we put "unknown"
        if(id != "Unknown"){
            // Fetch the JSON and add data into movie object
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
                    // Replace " with `
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
                
                
                // Differents error (JSON / IO)
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
    
    private static Movie getMovieActorsDirectors(Movie movie, String id){
       String actors = "";
       String directors = "";
       
       if(id != "Unknown"){
            // Fetch the JSON and add data into movie object
            try {
                // Get a JSON from an URL
                JSONObject json = ParsingJSON.readJsonFromUrl("https://api.themoviedb.org/3/movie/"+id+"/credits?api_key="+apiKey);

                /* CASTING */
                JSONArray jsonArrayCast = json.getJSONArray("cast");      
                // If there is one or more people on the casting
                if(jsonArrayCast.isNull(0)){
                    actors = "Unknown";
                }else{
                    // We get all peoples
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
                // If there is one or more people on the casting
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

            // Differents error (JSON / IO)
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
    
    
}
