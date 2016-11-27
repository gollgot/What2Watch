/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sun.awt.RepaintArea;

/**
 *
 * @author Raphael.BAZZARI
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Button settingsButton;
    @FXML
    private Button scanFolder;
    @FXML
    private TextArea synopsisTextArea;
    @FXML
    private ListView<String> movieListView;
    @FXML
    private TextField searchTextField;
    @FXML
    private ComboBox<String> searchCriteriasComboBox;
    @FXML
    private ImageView movieImageView;
    @FXML
    private TextField startingYearTextField;
    @FXML
    private TextField endingYearTextField;
    @FXML
    private Label titleLabel;
    @FXML
    private Label titleValueLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label yearValueLabel;
    @FXML
    private Label genreLabel;
    @FXML
    private Label genreValueLabel;
    @FXML
    private Label ActorsLabel;
    @FXML
    private Label actorsValueLabel;
    @FXML
    private Label synopsisLabel;
    @FXML
    private Label startingYearLabel;
    @FXML
    private Label endingYearLabel;
    @FXML
    private Label directorsLabel;
    @FXML
    private Label directorsValueLabel;
    
    private UserPreferences prefs = new UserPreferences();
    private ObservableList movieFileNames = FXCollections.observableArrayList();
    private ObservableList<Movie> movies = FXCollections.observableArrayList();
    private boolean searchIsEnabled; // Indicates whether the UI is ready to handle searches or not
    private int activeSearchMode = 0;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Displaying the settings window before the main one if no path has been saved in the app
        if(this.prefs.getPath().equals("")) {
            try {
                showSettings(null);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // Combobox search criterias configuration
        // those values have to match the switch case statement in the updateSearchMode method below
        this.searchCriteriasComboBox.getItems().addAll(
            "Title", // index 0
            "Genre", // index 1
            "Year", // index 2
            "Director", // index 3
            "Actor" // index 4
        );
        
        // Disabling the search bars to prevent searches from being processed
        // until the movie list is displayed in the listView
       enableSearchBars(false);
    }    

    @FXML
    private void showSettings(ActionEvent event) throws IOException {
        // Settings window creation
        Parent root = FXMLLoader.load(getClass().getResource("FXMLSettings.fxml"));
        Stage settingStage = new Stage();
        
        // Window customization
        settingStage.setResizable(false);
        settingStage.initModality(Modality.APPLICATION_MODAL);
        settingStage.setTitle("Sélection du répertoire de films");
        
        Scene scene = new Scene(root);
        settingStage.setScene(scene);
        
        settingStage.showAndWait();
    }

    @FXML
    private void browseFiles(ActionEvent event) throws IOException {
        CacheDb cacheDb = new CacheDb();
        movies.clear();
        // Update the cache db (add or remove movies on DB, it depend on the
        // browser.getMovieFileNames) and get real titles of all the movies on 
        // the DB.
        ArrayList<String> fileNames = ParsingFiles.parse(FileBrowser.getMovieFileNames());
        ArrayList<String> rawFileNames = FileBrowser.getMovieFileNames();
        DbHandler dbHandler = new DbHandler(cacheDb,fileNames,rawFileNames);
        dbHandler.update();
        
        // We wait the end of the thread
        // (thread.join() allow to continue when thread is finished)
        try {
            dbHandler.getUpdateThread().join();
        } catch (InterruptedException ex) {
            System.out.println("Error on browseFiles method on FXMLDocumentController class Ex: "+ex.getMessage().toString());
        }

        // Get the array holding all the infos
        String[] realTitles = dbHandler.getAllTitles();
        
        // Filing the listView with movie file names
        this.movieFileNames.clear();
        this.movieFileNames.addAll(realTitles);
        this.movieListView.setItems(this.movieFileNames);
        
        // Providing the search hander with informations needed to process movie searches
        SearchHandler.initializeSearchHandler(movieListView, this.movieFileNames);
        
        // Allowing the user to use the search bar
        enableSearchBars(true);
    }

    @FXML
    private void updateSearchMode(ActionEvent event) {
        int boxIndex = this.searchCriteriasComboBox.getSelectionModel().getSelectedIndex();
        
        // NOTE: The combobox item values have to be defined so that they match the following statement

        switch (boxIndex) {
            case 0: // Title
                setYearSearchMode(false);
                this.activeSearchMode = 0;
                break;
            case 1: // Genre
                setYearSearchMode(false);
                this.activeSearchMode = 1;
                break;
            case 2: // Year
                setYearSearchMode(true);
                this.activeSearchMode = 2;
                break;
            case 3: // Director
                setYearSearchMode(false);
                this.activeSearchMode = 3;
                break;
            case 4: // Actor
                setYearSearchMode(false);
                this.activeSearchMode = 4;
                break;
            default:
                break;
        }
    }
    
    // Disables search textfields and toggles the searchIsEnabled property
    private void enableSearchBars(boolean toggleValue) {
        double opacityValue;
        
        if (toggleValue == true) {
            opacityValue = 1.0;
        } else {
            opacityValue = 0.2;
        }
        
        this.searchTextField.setOpacity(opacityValue);
        this.startingYearTextField.setOpacity(opacityValue);
        this.endingYearTextField.setOpacity(opacityValue);
        
        this.searchTextField.setEditable(toggleValue);
        this.startingYearTextField.setEditable(toggleValue);
        this.endingYearTextField.setEditable(toggleValue);
        
        this.searchIsEnabled = toggleValue;
    }
    
    // Displays/hides textfields according to the selected combobox search criteria
    private void setYearSearchMode(boolean on) {
        startingYearLabel.setVisible(on);
        startingYearTextField.setVisible(on);
        endingYearLabel.setVisible(on);
        endingYearTextField.setVisible(on);
        searchTextField.setVisible(!on);
    }

    @FXML
    private void getMovieInformations(MouseEvent event) {
    // Connect to the DB
        CacheDb cacheDb = new CacheDb();
        // Get title of clicked film
        String movieTitle = movieListView.getSelectionModel().getSelectedItem().toString();
        // Set all data of the Movie
        String query = "SELECT * FROM movie WHERE title=\""+movieTitle+"\"";
        String results[] = cacheDb.doSelectQuery(query).split(";");
        String movieId = results[0];
        String movieRawTitle = results[1];
        movieTitle = results[2];
        String movieYear = results[3];
        String movieImageLink = results[4];
        String movieSynopsis = results[5];
        
        query = "SELECT name FROM actor INNER JOIN movie_has_actor ON actor.id = movie_has_actor.actor_id "
                + "INNER JOIN movie ON movie.id = movie_has_actor.movie_id "
                + "WHERE movie.title=\""+movieTitle+"\"";
        String movieActors = cacheDb.doSelectQuery(query).replaceAll(";", ", ");
        // Delete last comma
        movieActors = movieActors.replaceAll(", $", "");
        
        query = "SELECT type FROM genre INNER JOIN movie_has_genre ON genre.id = movie_has_genre.genre_id "
                + "INNER JOIN movie ON movie.id = movie_has_genre.movie_id "
                + "WHERE movie.title=\""+movieTitle+"\"";
        String movieGenres = cacheDb.doSelectQuery(query).replaceAll(";", ", ");
        // Delete last comma
        movieGenres = movieGenres.replaceAll(", $", "");
        
        query = "SELECT name FROM director INNER JOIN movie_has_director ON director.id = movie_has_director.director_id "
                + "INNER JOIN movie ON movie.id = movie_has_director.movie_id "
                + "WHERE movie.title=\""+movieTitle+"\"";
        String movieDirectors = cacheDb.doSelectQuery(query).replaceAll(";", ", ");
        // Delete last comma
        movieDirectors = movieDirectors.replaceAll(", $", "");
        
        query = "SELECT image_link FROM movie WHERE movie.title=\""+movieTitle+"\"";
        String moviePosterURL = cacheDb.doSelectQuery(query).replaceAll(";", ", ");
        // Delete last comma
        moviePosterURL = moviePosterURL.replaceAll(", $", "");
        
        // Set texts on the labels
        titleValueLabel.setText(movieTitle);
        yearValueLabel.setText(movieYear);
        synopsisTextArea.setText(movieSynopsis);  
        actorsValueLabel.setText(movieActors);
        genreValueLabel.setText(movieGenres);
        directorsValueLabel.setText(movieDirectors);  
        
        // Movie poster handling
        Image moviePoster = new Image("what2watch/images/placeHolder.png");
        if (!moviePosterURL.equals("Unknown")) {
            moviePoster = new Image("http://image.tmdb.org/t/p/w300" + moviePosterURL);
        }
        movieImageView.setImage(moviePoster);
    }

    @FXML
    private void searchForMatchingMovies(KeyEvent event) {
        if (this.searchIsEnabled) {
            // Calling the right search methods according to the active search mode
            switch (activeSearchMode) {
                case 0: // Title
                    SearchHandler.findMovieByTitle(this.searchTextField.getText());
                    break;
                case 1: // Genre
                    SearchHandler.findMovieByGenre(this.searchTextField.getText());
                    break;
                case 2: // Year
                    SearchHandler.findMovieByYearRange(this.startingYearTextField.getText(), this.endingYearTextField.getText());
                    break;
                case 3: // Director
                    SearchHandler.findMovieByDirector(this.searchTextField.getText());
                    break;
                case 4: // Actor
                    SearchHandler.findMovieByActor(this.searchTextField.getText());
                    break;
                default:
                    break;
            }
        }
    }
    
}
