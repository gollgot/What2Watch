/*
 * The purpose of this class is to handle the search triggerd by the user
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
    private static ListView<Movie> movieListView;
    private static ObservableList<Movie> originalMovieList;
    
    // Provides the class with informations that will be used in its methods
    // in order to process a movie search
    public static void initializeSearchHandler (ListView<Movie> listView, ObservableList<Movie> movieList) {
       movieListView = listView; 
       originalMovieList = movieList;
    }
    
    // Compares the title of every movies contained in the listView on the main Window
    // against the search term provided in parameter.
    // Either displays a list of movies that match the search or the original one
    public static void findMoviesByTitle(String searchTerm) {
        // Eventually holds the list of films that will be displayed in the listView
        ObservableList<Movie> finalList = FXCollections.observableArrayList();
        
        // Making sure there is a term to base the search on
        if (!searchTerm.equals("")) {
            ObservableList<Movie> matchingMovies = FXCollections.observableArrayList();
            
            // Comparing the search term against each title of movies in the list
            for (Movie currentMovie : originalMovieList) {
                String movieName = currentMovie.getTitle();
                if (movieName.toLowerCase().contains(searchTerm.toLowerCase())) {
                   matchingMovies.add(currentMovie); 
                }
            }
            // Setting the final with a list of movies that match the search term
            finalList = matchingMovies;
            
        } else {
            // No search term to compare against -> setting the final list to the original movie list
            finalList = originalMovieList;
        }
        
        // Updating the list view with either a list of movies matching the search term
        // or the list that was originally displayed before the search occured
        movieListView.setItems(finalList);
    }
    
}
