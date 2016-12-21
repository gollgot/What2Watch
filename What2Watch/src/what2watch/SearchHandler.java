/*
 * The purpose of this class is to handle searches triggerd by the user
 * when typing in one of the search textfields in the main window. Also, this class is responsible
 * for updating informations displayed within the movie listView.
 */
package what2watch;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 *
 * @author Raphael.BAZZARI
 */
public class SearchHandler {
    private static ListView<String> movieListView;
    private static ObservableList<String> originalMovieList;


    /** 
     * Provides the SearchHandler with informations that are needed in
     * in order to process movie researches
     *  
     * @param   listView The listView of movies that is displayed in the main window
     * @param   movieList The list containing the original movie titles
     */
    public static void initializeSearchHandler(ListView<String> listView, ObservableList<String> movieList) {
        movieListView = listView;
        originalMovieList = movieList;
    }

    /** 
     * Executes queries and updates the main window movie list view with informations
     * gathered in query results
     *  
     * @param   query A String representing the query that has to be executed
     * in order to get movie informations
     */
    public static void findMoviesFromQuery(String query) {
        // Eventually holds the list of films that will be displayed in the listView
        ObservableList<String> matchingMovies = FXCollections.observableArrayList();

        CacheDb db = new CacheDb();
        String queryResult = db.doSelectQuery(query);

        // Making sure the query returned something 
        if (!queryResult.equals("")) {
            queryResult = queryResult.substring(0, queryResult.length() - 1);
            String movieTtiles[] = queryResult.split(";");

            // Filling the finalList with movies matching the search term 
            for (String movie : movieTtiles) {
                matchingMovies.add(movie);
            }
        }

        // Updating the listView
        movieListView.setItems(matchingMovies);
    }

    /** 
     * Builds a query that will retrieve movies by comparing the search term
     * against the availble movie titles in the DB. Once built, the query is
     * dispatched to SearchHandler's "findMoviesFromQuery" method. If the provided
     * search term is empty, the main window movie listView values are reset.
     *  
     * @param   searchTerm A String representing what the user typed in
     * the search textfield of the main window
     * 
     * @see SearchHandler#findMoviesFromQuery
     */
    public static void findMovieByTitle(String searchTerm) {
        if (!searchTerm.equals("")) {
            searchTerm = escapeChars(searchTerm);
            String query = "SELECT title from movie "
                    + "WHERE title like '%" + searchTerm + "%'";
            findMoviesFromQuery(query);
        } else {
            movieListView.setItems(originalMovieList);
        }
    }

    /** 
     * Builds a query that will retrieve movies by comparing the search term
     * against the availble movie genres in the DB. Once built, the query is
     * dispatched to SearchHandler's "findMoviesFromQuery" method. If the provided
     * search term is empty, the main window movie listView values are reset.
     *  
     * @param   searchTerm A String representing what the user typed in
     * the search textfield of the main window
     * 
     * @see SearchHandler#findMoviesFromQuery
     */
    public static void findMovieByGenre(String searchTerm) {
        if (!searchTerm.equals("")) {
            searchTerm = escapeChars(searchTerm);
            String query = "SELECT DISTINCT title from movie "
                    + "INNER JOIN movie_has_genre ON movie.id = movie_has_genre.movie_id "
                    + "INNER JOIN genre ON movie_has_genre.genre_id = genre.id "
                    + "WHERE genre.type like '%" + searchTerm + "%'";
            findMoviesFromQuery(query);
        } else {
            movieListView.setItems(originalMovieList);
        }
    }

    /** 
     * Builds a query that will retrieve movies by comparing the search term
     * against the availble movie directors in the DB. Once built, the query is
     * dispatched to SearchHandler's "findMoviesFromQuery" method. If the provided
     * search term is empty, the main window movie listView values are reset.
     *  
     * @param   searchTerm A String representing what the user typed in
     * the search textfield of the main window
     * 
     * @see SearchHandler#findMoviesFromQuery
     */
    public static void findMovieByDirector(String searchTerm) {
        if (!searchTerm.equals("")) {
            searchTerm = escapeChars(searchTerm);
            String query = "SELECT DISTINCT title from movie "
                    + "INNER JOIN movie_has_director ON movie.id = movie_has_director.movie_id "
                    + "INNER JOIN director ON movie_has_director.director_id = director.id "
                    + "WHERE director.name like '%" + searchTerm + "%'";
            findMoviesFromQuery(query);
        } else {
            movieListView.setItems(originalMovieList);
        }
    }

    /** 
     * Builds a query that will retrieve movies based on a year range which is compared
     * to the available movies release dates in the DB. Once built, the query is
     * dispatched to SearchHandler's "findMoviesFromQuery" method. If the provided
     * search terms are empty, the main window movie listView values are reset.
     *  
     * @param   startingYear A String representing the starting year of the range
     * @param   endingYear A String representing the ending year of the range
     * 
     * @see SearchHandler#findMoviesFromQuery
     */
    public static void findMovieByYearRange(String startingYear, String endingYear) {
        // Handles ex: from 1990 to 2000 and from 1990 to nothing
        if ((!startingYear.equals("") && !endingYear.equals("")) ||
                ((!startingYear.equals("") && endingYear.equals(""))) ) {
            String query = "SELECT title from movie "
                    + "WHERE year BETWEEN '" + startingYear + "' AND '" + endingYear + "'";
            findMoviesFromQuery(query);
            // Handles ex: from nothing to 2016
        } else if (startingYear.equals("") && !endingYear.equals("")) {
            String query = "SELECT title from movie WHERE year <= " + endingYear;
            findMoviesFromQuery(query);
        } else {
            movieListView.setItems(originalMovieList);
        }
    }

    
    /** 
     * Builds a query that will retrieve movies by comparing the search term
     * against the availble movie actors in the DB. Once built, the query is
     * dispatched to SearchHandler's "findMoviesFromQuery" method. If the provided
     * search term is empty, the main window movie listView values are reset.
     *  
     * @param   searchTerm A String representing what the user typed in
     * the search textfield of the main window
     * 
     * @see SearchHandler#findMoviesFromQuery
     */
    public static void findMovieByActor(String searchTerm) {
        searchTerm = escapeChars(searchTerm);
        if (!searchTerm.equals("")) {
            String query = "SELECT DISTINCT movie.title FROM movie "
                    + "INNER JOIN movie_has_actor ON movie.id = movie_has_actor.movie_id "
                    + "INNER JOIN actor ON movie_has_actor.actor_id = actor.id "
                    + "WHERE actor.name like '%" + searchTerm + "%'";
            findMoviesFromQuery(query);
        } else {
            movieListView.setItems(originalMovieList);
        }
    }
    
    /** 
     * Escapes special characters of the search term prior to its inclusion
     * in a query. This method is called by every method that build a search query.
     * 
     * @param   searchTerm A String representing what the user typed in
     * the search textfield of the main window
     * 
     * @see SearchHandler#findMovieByTitle
     * @see SearchHandler#findMovieByGenre
     * @see SearchHandler#findMovieByDirector
     * @see SearchHandler#findMovieByYearRange
     * @see SearchHandler#findMovieByActor
     */
    private static String escapeChars(String searchTerm) {
        String singleQuoteFree = searchTerm.replace("'", "''");
        String doubleQuoteFree = singleQuoteFree.replace("\"", "\"");
        String underScoreFree = doubleQuoteFree.replace("_", "");
        String percentFree = underScoreFree.replace("%", "");
        return percentFree;
    }
}
