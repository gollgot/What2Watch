/*
 * The purpose of this class is to handle searches triggerd by the user
 * when typing in one of the search bars in the main window. Also, this class is responsible
 * for updating the informations displayed in the movie list.
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

    // Provides the class with informations that will be used in its methods
    // in order to process a movie search
    public static void initializeSearchHandler(ListView<String> listView, ObservableList<String> movieList) {
        movieListView = listView;
        originalMovieList = movieList;
    }

    // Displays movies fetched via the query passed in parameter
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

    public static void findMovieByTitle(String searchTerm) {
        if (!searchTerm.equals("")) {
            String query = "SELECT title from movie "
                    + "WHERE title like '%" + searchTerm + "%'";
            findMoviesFromQuery(query);
        } else {
            movieListView.setItems(originalMovieList);
        }
    }

    public static void findMovieByGenre(String searchTerm) {
        if (!searchTerm.equals("")) {
            String query = "SELECT title from movie "
                    + "INNER JOIN movie_has_genre ON movie.id = movie_has_genre.movie_id "
                    + "INNER JOIN genre ON movie_has_genre.genre_id = genre.id "
                    + "WHERE genre.type like '%" + searchTerm + "%'";
            findMoviesFromQuery(query);
        } else {
            movieListView.setItems(originalMovieList);
        }
    }

    public static void findMovieByDirector(String searchTerm) {
        if (!searchTerm.equals("")) {
            String query = "SELECT title from movie "
                    + "INNER JOIN movie_has_director ON movie.id = movie_has_director.movie_id "
                    + "INNER JOIN director ON movie_has_director.director_id = director.id "
                    + "WHERE director.name like '%" + searchTerm + "%'";
            findMoviesFromQuery(query);
        } else {
            movieListView.setItems(originalMovieList);
        }
    }

    public static void findMovieByYearRange(String startingYear, String endingYear) {
        if (!startingYear.equals("") || !endingYear.equals("")) {
            String query = "SELECT title from movie "
                    + "WHERE year BETWEEN '" + startingYear + "' AND '" + endingYear + "'";
            findMoviesFromQuery(query);
        } else {
            movieListView.setItems(originalMovieList);
        }
    }

    public static void findMovieByActor(String searchTerm) {
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
}
