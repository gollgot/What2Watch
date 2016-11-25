/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author David.ROSAT & loic.dessaules
 */
public class DbHandler {

    private CacheDb dataBase;
    private ArrayList<String> originalMovieNames;
    private ArrayList<String> rawMovieNames;

    public DbHandler(CacheDb dataBase, ArrayList<String> originalMovieNames, ArrayList<String> rawMovieNames) {
        this.dataBase = dataBase;
        this.originalMovieNames = originalMovieNames;
        this.rawMovieNames = rawMovieNames;
    }

    public void update() {
        // Thread for update the database, because we have to get the datas from the
        //API and wait x ms after each request (see method "getAllMovieInfos" in class "ApiHandler" for more infos
        Thread test = new Thread(new Runnable() {
            @Override
            public void run() {
                // Check if the movie already exists on the DB, if not, we put on all the datas
                for (int i = 0; i < originalMovieNames.size(); i++) {
                    if (movieExistsOnDb(rawMovieNames.get(i))) {
                        System.out.println(originalMovieNames.get(i) + " Existe !");
                    } else {
                        System.out.println(originalMovieNames.get(i) + " Existe pas !");
                        Movie movie = ApiHandler.getAllMovieInfos(originalMovieNames.get(i), rawMovieNames.get(i));
                        insertMovieOnDb(movie);
                    }
                }
                
            }
        });
        test.start();
        
        deleteMovieOnDb();
    }

    private boolean movieExistsOnDb(String rawMovieName) {

        // The raw_title is the only data who is the same in the user directory and on the DB,
        // so, if this is the same, the movie is already on the DB
        String query = "SELECT raw_title FROM movie WHERE raw_title = \"" + rawMovieName + "\"";
        String result = dataBase.doSelectQuery(query);

        if (result.equals("")) {
            return false;
        } else {
            return true;
        }
    }
    

    // BE CAREFUL -> we have to replace replace ' with " for keep ' on all text
    // on query example : INSERT INTO('description') VALUES('i'm') => so => 
    // INSERT INTO('description') VALUES("i'm")
    void insertMovieOnDb(Movie movie) {
        System.out.println("\n**** INSERT MOVIE *****");

        /* Insert Movie */
        String queryInsertMovie = "INSERT INTO 'movie' "
                + "VALUES"
                + "(NULL,\"" + movie.getRawTitle() + "\",\"" + movie.getTitle() + "\",\"" + movie.getYear() + "\",\"" + movie.getPoster() + "\",\"" + movie.getSynopsis() + "\")";
        dataBase.doNoReturnQuery(queryInsertMovie);
        System.out.println(movie.getTitle() + " ajouté");
        
        // Get id of the movie
        String querySelectIdMovie = "SELECT id FROM movie WHERE title = \""+movie.getTitle()+"\"";
        String idMovie = dataBase.doSelectQuery(querySelectIdMovie).replace(";", "");  
        
        /* Insert actor and movie_has_actor if he doesn't already exists and if we know the name of the actors */
        String actors[] = movie.getActors();
        if(actors[0] != "Inconnu"){ 
            
            for (int i = 0; i < actors.length; i++) {
                String querySelect = "SELECT name FROM actor WHERE name = \"" + actors[i] + "\"";
                String result = dataBase.doSelectQuery(querySelect);
                if (result.equals("")) {
                    String queryInsertActor = "INSERT INTO 'actor' "
                            + "VALUES(NULL,\"" + actors[i] + "\")";
                    dataBase.doNoReturnQuery(queryInsertActor);
                    System.out.println(actors[i] + " ajouté");
                } else {
                    System.out.println(actors[i] + " existe déjà");
                }
                /* Insert movie_has_actor*/
                // For each actors on the movie, we get their id and insert the both of
                // id on movie_has_actor table
                String querySelectIdActor = "SELECT id FROM actor WHERE name =\"" + actors[i] + "\"";
                String idActor = dataBase.doSelectQuery(querySelectIdActor).replace(";", "");
                String queryInsertMovieHasActor = "INSERT INTO 'movie_has_actor' "
                        + "VALUES"
                        + "('" + idMovie + "','" + idActor + "')";
                dataBase.doNoReturnQuery(queryInsertMovieHasActor);
                System.out.println("movie_has_actor add : " + idMovie + "," + idActor);
            }
        }
        
        /* Insert genre and movie_has_genre if he doesn't already exists and if we know the name of the genres */
        String genres[] = movie.getGenre();
        if(genres[0] != "Inconnu"){
            for (int i = 0; i < genres.length; i++) {
                String querySelect = "SELECT type FROM genre WHERE type = \"" + genres[i] + "\"";
                String result = dataBase.doSelectQuery(querySelect);
                if (result.equals("")) {
                    String queryInsertGenre = "INSERT INTO 'genre' "
                            + "VALUES(NULL,'" + genres[i] + "')";
                    dataBase.doNoReturnQuery(queryInsertGenre);
                    System.out.println(genres[i] + " ajouté");
                } else {
                    System.out.println(genres[i] + " existe déjà");
                }
                /* Insert movie_has_genre*/
                // For each genre of the movie, we get their id and insert the both of
                // id on movie_has_genre table             
                String querySelectIdGenre = "SELECT id FROM genre WHERE type =\"" + genres[i] + "\"";
                String idGenre = dataBase.doSelectQuery(querySelectIdGenre).replace(";", "");
                String queryInsertMovieHasGenre = "INSERT INTO 'movie_has_genre' "
                        + "VALUES"
                        + "('" + idMovie + "','" + idGenre + "')";
                dataBase.doNoReturnQuery(queryInsertMovieHasGenre);
                System.out.println("movie_has_genre add : " + idMovie + "," + idGenre);
            }
        }

        /* Insert director and movie_has_director if he doesn't already exists and if we know the name of the directors*/
        String directors[] = movie.getDirector();
        if(directors[0] != "Inconnu"){
            for (int i = 0; i < directors.length; i++) {
                String querySelect = "SELECT name FROM director WHERE name = \"" + directors[i] + "\"";
                String result = dataBase.doSelectQuery(querySelect);
                if (result.equals("")) {
                    String queryInsertDirector = "INSERT INTO 'director' "
                            + "VALUES(NULL,\"" + directors[i] + "\")";
                    dataBase.doNoReturnQuery(queryInsertDirector);
                    System.out.println(directors[i] + " ajouté");
                } else {
                    System.out.println(directors[i] + " existe déjà");

                }
                /* Insert movie_has_director*/
                // For each director of the movie, we get their id and insert the both of
                // id on movie_has_director table
                String querySelectIdDirector = "SELECT id FROM director WHERE name =\"" + directors[i] + "\"";
                String idDirector = dataBase.doSelectQuery(querySelectIdDirector).replace(";", "");
                String queryInsertMovieHasDirector = "INSERT INTO 'movie_has_director' "
                        + "VALUES"
                        + "('" + idMovie + "','" + idDirector + "')";
                dataBase.doNoReturnQuery(queryInsertMovieHasDirector);
                System.out.println("movie_has_director add : " + idMovie + "," + idDirector);
            }
        }

        System.out.println("**** MOVIE INSERTED AND DATAS UPDATED *****\n");

    }

    // If we have 10 movies on our folder and, further, we'll have only 5,
    // so we have to delete the 5 non-existant movie from the DB
    private void deleteMovieOnDb() {
        UserPreferences prefs = new UserPreferences();
        String path = prefs.getPath();
        String strTotalMovies;
        int totalMovies;
        String[] rawTitleMoviesDb;
        FileBrowser fileBrowser;

        // Get number of movie on the DB and all their raw_title
        String query1 = "SELECT COUNT(title) FROM movie";
        String query2 = "SELECT raw_title FROM movie";

        strTotalMovies = dataBase.doSelectQuery(query1);
        strTotalMovies = strTotalMovies.replace(";", "");
        totalMovies = Integer.parseInt(strTotalMovies);

        rawTitleMoviesDb = dataBase.doSelectQuery(query2).split(";");

        // For all the movie, we check if the file always exists on the
        // user directory or not (with the raw_path)
        for (int i = 0; i < totalMovies; i++) {
            fileBrowser = new FileBrowser();
            try {
                String fullPath = fileBrowser.getFilePath(rawTitleMoviesDb[i]);
                if (fullPath == "") {
                    File movie = new File(fullPath);
                    System.out.println("DEBUG----- " + fullPath + " -----DEBUG");
                    if (!movie.exists()) {
                        String query = "SELECT id FROM movie WHERE raw_title = '" + rawTitleMoviesDb[i] + "'";
                        String idMovie = dataBase.doSelectQuery(query).replace(";", "");

                        // Delete all row with foreign key and delete the movie
                        // (We keep actors, directors and genres because they can be used by other movies)
                        query = "DELETE FROM movie_has_actor WHERE movie_id = \"" + idMovie + "\"";
                        dataBase.doNoReturnQuery(query);
                        query = "DELETE FROM movie_has_genre WHERE movie_id = \"" + idMovie + "\"";
                        dataBase.doNoReturnQuery(query);
                        query = "DELETE FROM movie_has_director WHERE movie_id = \"" + idMovie + "\"";
                        dataBase.doNoReturnQuery(query);

                        //FOR DEBUG
                        query = "SELECT title FROM movie WHERE id = \"" + idMovie + "\"";
                        String title = dataBase.doSelectQuery(query).replace(";", "");
                        // END DEBUG

                        query = "DELETE FROM movie WHERE id = \"" + idMovie + "\"";
                        dataBase.doNoReturnQuery(query);

                        System.out.println("\"" + title + "\"" + " has been successfully deleted");
                        System.out.println("suppression d un film");
                    }
                }
            } catch (IOException ex) {
                System.out.println("ERROR DbHandler.deleteMovieOnDb : " + ex.getMessage().toString());
            }
        }

    }

    public String[] getAllTitles() {

        String query = "SELECT title FROM movie";
        String result = dataBase.doSelectQuery(query);
        String[] results = result.split(";");

        return results;
    }

}
