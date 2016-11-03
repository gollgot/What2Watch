/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author David.ROSAT & loic.dessaules
 */
public class DbHandler {
    
    private CacheDb dataBase;
    private ArrayList<String> originalMovieNames;

    public DbHandler(CacheDb dataBase, ArrayList<String> originalMovieName) {
        this.dataBase = dataBase;
        this.originalMovieNames = originalMovieName;
    }
    
    public void update(){
        // Check if the movie already exists on the DB, if not, we put on all the datas
        for (int i = 0; i < originalMovieNames.size(); i++) {
            if(movieExistsOnDb(originalMovieNames.get(i))){
                System.out.println(originalMovieNames.get(i)+" Existe !");
                // ==> we have to created method who getInfos of mthe movie, create a Movie object etc...
            }else{
                System.out.println(originalMovieNames.get(i)+" Existe pas !");
                getMovieInfosFromAPI(originalMovieNames.get(i));
            }
        }
        
    }
    
    private boolean movieExistsOnDb(String movieName){
        
        // I get the movieName in lower case and I check in the DB on a LOWER(title)
        // like that we don't have any conflict with sensitiveCase
        String movieNameLower = movieName.toLowerCase();
        String query = "SELECT LOWER(title) AS title FROM movie WHERE title LIKE \""+movieNameLower+"\"";
        
        String result = dataBase.doSelectQuery(query);       
        
        if(result.equals("")){
            return false;   
        }else{
            return true;        
        }
    }

    private void getMovieInfosFromAPI(String movieName) {

        Boolean internetError = false;
        String movieNameUrlFormat = movieName.replaceAll(" ", "%20");
        
        Movie movie = new Movie();
        // Fetch the JSON and add data into movie object
        try {
            // Get a JSON from an URL
            JSONObject json = ParsingJSON.readJsonFromUrl("http://www.omdbapi.com/?t="+movieNameUrlFormat+"&y=&plot=full&r=json");
            // Set data on a movie object
            movie.setTitle(json.get("Title").toString());
            movie.setYear(json.get("Year").toString());
            movie.setDirector(json.get("Director").toString());
            movie.setActors(json.get("Actors").toString());
            movie.setGenre(json.get("Genre").toString());
            movie.setPoster(json.get("Poster").toString());
            // Some plots have \" on their text, so we have to replace all \" with ´
            // like that it's like a simple quote 
            String synopsis = json.get("Plot").toString();
            synopsis = synopsis.replaceAll("\\\"","`");
            movie.setSynopsis(synopsis);
            
            
       
        } catch (JSONException ex) {
            System.out.println("ERROR on parsingJSON (JSON exception) : "+ex.getMessage());
        } catch (IOException ex) {
            System.out.println("ERROR on parsingJSON (IO exception) : "+ex.getMessage() + "\nVeuillez vérifier votre connexion internet");
            internetError = true;
        }
        
        if(!internetError){
            insertMovieOnDb(movie);
        }else{
            System.out.println("Impossible de récupérer les informations du film "
                    + "\""+movieName+"\", Veuillez vérifié votre connexion "
                    + "internet et relancer le programme.");
        }
        
    }
    
    // BE CAREFUL -> we have to replace replace ' with " for keep ' on all text
    // on query example : INSERT INTO('description') VALUES('i'm') => so => 
    // INSERT INTO('description') VALUES("i'm")
    private void insertMovieOnDb(Movie movie) {
        System.out.println("\n**** INSERT MOVIE *****");
        
        /* Insert Movie */
        String queryInsertMovie = "INSERT INTO 'movie' "
                + "VALUES"
                + "(NULL,\""+movie.getTitle()+"\",\""+movie.getYear()+"\",\""+movie.getPoster()+"\",\""+movie.getSynopsis()+"\")";
        dataBase.doNoReturnQuery(queryInsertMovie);
        System.out.println(movie.getTitle()+" ajouté");
        
        
        /* Insert actor if he doesn't already exists */
        String actors[] = movie.getActors();
        for (int i = 0; i < actors.length; i++) {
            String querySelect = "SELECT name FROM actor WHERE name = \""+actors[i]+"\"";
            String result = dataBase.doSelectQuery(querySelect);
            if(result.equals("")){
                String queryInsertActor = "INSERT INTO 'actor' "
                        + "VALUES(NULL,\""+actors[i]+"\")";
                dataBase.doNoReturnQuery(queryInsertActor);
                System.out.println(actors[i]+" ajouté");
            }else{
                System.out.println(actors[i]+" existe déjà");
            }
        }
        
        
        /* Insert genre if he doesn't already exists */
        String genres[] = movie.getGenre();
        for (int i = 0; i < genres.length; i++) {
            String querySelect = "SELECT type FROM genre WHERE type = \""+genres[i]+"\"";
            String result = dataBase.doSelectQuery(querySelect);
            if(result.equals("")){
                String queryInsertGenre = "INSERT INTO 'genre' "
                        + "VALUES(NULL,'"+genres[i]+"')";
                dataBase.doNoReturnQuery(queryInsertGenre);
                System.out.println(genres[i]+" ajouté");
            }else{
                System.out.println(genres[i]+" existe déjà");
            }
        }
        
        /* Insert director if he doesn't already exists */
        String directors[] = movie.getDirector();
        for (int i = 0; i < directors.length; i++) {
            String querySelect = "SELECT name FROM director WHERE name = \""+directors[i]+"\"";
            String result = dataBase.doSelectQuery(querySelect);
            if(result.equals("")){
                String queryInsertDirector = "INSERT INTO 'director' "
                        + "VALUES(NULL,\""+directors[i]+"\")";
                dataBase.doNoReturnQuery(queryInsertDirector);
                System.out.println(directors[i]+" ajouté");
            }else{
                System.out.println(directors[i]+" existe déjà");
           
            }
        }
        
        /* Insert movie_has_actor*/
        // Get id of the movie
        String querySelectIdMovie = "SELECT id FROM movie WHERE title = '"+movie.getTitle()+"'";
        String idMovie = dataBase.doSelectQuery(querySelectIdMovie).replace(";", "");
        // For each actors on the movie, we get their id and insert the both of
        // id on movie_has_actor table
        for (int i = 0; i < actors.length; i++) {
            String querySelectIdActor = "SELECT id FROM actor WHERE name =\""+actors[i]+"\"";
            String idActor = dataBase.doSelectQuery(querySelectIdActor).replace(";", "");
            String queryInsertMovieHasActor = "INSERT INTO 'movie_has_actor' "
                    +"VALUES"
                    + "('"+idMovie+"','"+idActor+"')";
            dataBase.doNoReturnQuery(queryInsertMovieHasActor);
            System.out.println("movie_has_actor add : "+idMovie+","+idActor);
        }
        
        
        /* Insert movies_has_genre*/
        // For each genre of the movie, we get their id and insert the both of
        // id on movie_has_genre table
        for (int i = 0; i < genres.length; i++) {
            String querySelectIdGenre = "SELECT id FROM genre WHERE type =\""+genres[i]+"\"";
            String idGenre = dataBase.doSelectQuery(querySelectIdGenre).replace(";", "");
            String queryInsertMovieHasGenre = "INSERT INTO 'movie_has_genre' "
                    +"VALUES"
                    + "('"+idMovie+"','"+idGenre+"')";
            dataBase.doNoReturnQuery(queryInsertMovieHasGenre);
            System.out.println("movie_has_genre add : "+idMovie+","+idGenre);
        }
        
        
        /* Insert movies_has_director*/
        // For each director of the movie, we get their id and insert the both of
        // id on movie_has_director table
        for (int i = 0; i < directors.length; i++) {
            String querySelectIdDirector = "SELECT id FROM director WHERE name =\""+directors[i]+"\"";
            String idDirector = dataBase.doSelectQuery(querySelectIdDirector).replace(";", "");
            String queryInsertMovieHasDirector = "INSERT INTO 'movie_has_director' "
                    +"VALUES"
                    + "('"+idMovie+"','"+idDirector+"')";
            dataBase.doNoReturnQuery(queryInsertMovieHasDirector);
            System.out.println("movie_has_director add : "+idMovie+","+idDirector);
        }
        
        System.out.println("**** MOVIE INSERTED AND DATAS UPDATED *****\n");
        
    }
    
    public String[] getAllTitles() {
        
        String query = "SELECT title FROM movie";
        String result = dataBase.doSelectQuery(query);
        String[] results = result.split(";");
        
        return results;       
    }
   
    
}
