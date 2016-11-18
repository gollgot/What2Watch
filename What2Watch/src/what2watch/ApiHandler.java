/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public static void getAllMovieInfos(String movieName, String rawMovieName){
        try {
            String id = getMovieId(movieName);
            Thread.sleep(180);
            getMovieDetails(movieName, id);
            Thread.sleep(180);
        } catch (InterruptedException ex) {
            Logger.getLogger(ApiHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private static String getMovieId(String movieName) {
        Boolean internetError = false;
        String movieNameUrlFormat = movieName.replaceAll(" ", "%20");
        String id = "unknow";

        // Fetch the JSON and add data into movie object
        try {
            // Get a JSON from an URL
            JSONObject json = ParsingJSON.readJsonFromUrl("https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&language=en-US&query=" + movieNameUrlFormat);
            // Get the array data "results"
            JSONArray jsonArray = json.getJSONArray("results");
            
            // If there is no infos for the film
            if(jsonArray.isNull(0)){
                id = "unknow";
                System.out.println("DEBUG----- ID : "+id+" -----DEBUG");
            }else{
                // Get the first object of "results array"
                JSONObject jsonOject = jsonArray.getJSONObject(0);
                id = String.valueOf(jsonOject.getInt("id"));
                System.out.println("DEBUG----- ID : "+id+" -----DEBUG");
            }
        
        // Differents error (JSON / IO)
        } catch (JSONException ex) {
            System.out.println("ERROR on parsingJSON (JSON exception) : " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("ERROR on parsingJSON (IO exception) : " + ex.getMessage() + "\nVeuillez vérifier votre connexion internet");
            internetError = true;
        }
        
        return id;
        
    }
    
    private static void getMovieDetails(String movieName, String id){
        Boolean internetError = false;
        
        // If we have an id, we get the real title
        if(id != "unknow"){
            // Fetch the JSON and add data into movie object
            try {
                // Get a JSON from an URL
                JSONObject jsonObject = ParsingJSON.readJsonFromUrl("https://api.themoviedb.org/3/movie/" + id + "?api_key="+apiKey+"&language=en-US");

                String originalTitle = jsonObject.getString("title");
                System.out.println("DEBUG----- Title : "+originalTitle+" -----DEBUG");
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
        // If we don't have any id, we can't have any infos... so all infos will be "unknow"
        else{
            String originalTitle = "unknow";
            System.out.println("DEBUG----- Title : "+originalTitle+" -----DEBUG");
        }
    }
    
    
}
