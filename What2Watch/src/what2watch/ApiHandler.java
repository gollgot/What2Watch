/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author loic.dessaules
 */
public class ApiHandler {
    
    private static String apiKey = "9a52628ae3939c738592ac50fdd73f7c";
    
    
    public static void getMovieId(String movieName, String rawMovieName) {
        Boolean internetError = false;
        String movieNameUrlFormat = movieName.replaceAll(" ", "%20");

        Movie movie = new Movie();
        // Fetch the JSON and add data into movie object
        try {
            // Get a JSON from an URL
            JSONObject json = ParsingJSON.readJsonFromUrl("https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&language=en-US&query=" + movieNameUrlFormat);
            // Get the array data "results"
            JSONArray jsonArray = json.getJSONArray("results");
            
            // If there is no infos for the film
            if(jsonArray.isNull(0)){
                String id = "inconnu";
                System.out.println("DEBUG----- Aucun ID -----DEBUG");
            }else{
                // Get the first object of "results array"
                JSONObject jsonOject = jsonArray.getJSONObject(0);
                String id = String.valueOf(jsonOject.getInt("id"));
                System.out.println("DEBUG----- "+id+" -----DEBUG");
            }
        
        // Differents error (JSON / IO)
        } catch (JSONException ex) {
            System.out.println("ERROR on parsingJSON (JSON exception) : " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("ERROR on parsingJSON (IO exception) : " + ex.getMessage() + "\nVeuillez vérifier votre connexion internet");
            internetError = true;
        }
        
        // If there is no problem with internet connection
        if (!internetError) {
            //insertMovieOnDb(movie);
        } else {
            System.out.println("Impossible de récupérer les informations du film "
                    + "\"" + movieName + "\", Veuillez vérifié votre connexion "
                    + "internet et relancer le programme.");
        }

    }
    
    
}
