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
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    private ListView<?> movieListView;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
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
    private ObservableList movieFileNames = 
        FXCollections.observableArrayList();
    
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
        this.searchCriteriasComboBox.getItems().addAll(
            "Titre",
            "Acteur",
            "Année"
        );
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
        
        // Update the cache db (add or remove movies on DB, it depend on the
        // browser.getMovieFileNames) and get real titles of all the movies on 
        // the DB.
        ArrayList<String> fileNames = ParsingFiles.parse(FileBrowser.getMovieFileNames());
        ArrayList<String> rawFileNames = FileBrowser.getMovieFileNames();
        DbHandler dbHandler = new DbHandler(cacheDb,fileNames,rawFileNames);
        dbHandler.update();
        String[] realTitles = dbHandler.getAllTitles();
        
        // Filing the listView with movie file names
        this.movieFileNames.clear();
        this.movieFileNames.addAll(realTitles);
        this.movieListView.setItems(this.movieFileNames);       
    }

    @FXML
    private void updateSearchMode(ActionEvent event) {
        String searchCriteria = this.searchCriteriasComboBox.getValue();

        switch (searchCriteria) {
            case "Titre":
                // TODO limit the scope to movie title informations
                setYearSearchMode(false);
                break;
            case "Acteur":
                // TODO limit the scope to actors informations  
                setYearSearchMode(false);
                break;
            case "Année":
                setYearSearchMode(true);
                break;
            default:
                break;
        }
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
        if (!moviePosterURL.equals("Inconnu")) {
            Image moviePoster = new Image(moviePosterURL);
            movieImageView.setImage(moviePoster);
        }
    }
    
}
