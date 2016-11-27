/*
 * The purpose of this class is to handle searches triggerd by the user
 * when typing in the search bar in the main window. Also, this class is responsible
 * for updating the informations displayed in the movie list.
 */
package what2watch;

import java.util.ArrayList;
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

    // Provides the class with informations that will be used in its methods
    // in order to process a movie search
    public static void initializeSearchHandler(ListView<String> listView, ObservableList<String> movieList) {
        movieListView = listView;
        originalMovieList = movieList;
    }
    
    public static void findMovieByTitle(String searchTerm) {
        // Eventually holds the list of films that will be displayed in the listView
        ObservableList<String> finalList;

        // Making sure there's a search term to compare against
        if (!searchTerm.equals("")) {
            ObservableList<String> matchingMovies = FXCollections.observableArrayList();
            
            CacheDb db = new CacheDb();
            String query = "SELECT title from movie "
                    + "WHERE title like '%" + searchTerm + "%'";

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
            
            finalList = matchingMovies;
        } else {
            finalList = originalMovieList;
        }

        // Updating the listView
        movieListView.setItems(finalList);
    }
}
