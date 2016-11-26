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
    private static ObservableList<Movie> originaleMovieList;
    
    // Defines the list view that will be updated by this class methods
    public static void initializeSearchHandler (ListView<Movie> listView, ObservableList<Movie> movieList) {
       movieListView = listView; 
       originaleMovieList = movieList;
    }
    
    public static void findMoviesByTitle(String searchTerm) {
        ObservableList<Movie> matchingMovies = FXCollections.observableArrayList();
        
        for(Movie currentMovie: originaleMovieList) {
            String movieName = currentMovie.getTitle();
            if (searchTerm.equals("")) {
                System.out.println("1" + originaleMovieList);
                movieListView.setItems(originaleMovieList);
            } else {
                if (movieName.toLowerCase().contains(searchTerm.toLowerCase())) {
                   matchingMovies.add(currentMovie); 
                }
            }
        }
        
        // Updating the movie list movies matching the search terms
        movieListView.setItems(matchingMovies);
    }
    
}
