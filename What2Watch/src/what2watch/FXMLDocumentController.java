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
    private FileBrowser browser = new FileBrowser();
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
        ParsingFiles parsingFiles = new ParsingFiles();
        CacheDb cacheDb = new CacheDb();
        String path = this.prefs.getPath();
        browser.fetchMoviesFileNames(path);
        
        // Update the cache db (add or remove movies on DB, it depend on the
        // browser.getMovieFileNames) and get real titles of all the movies on 
        // the DB.
        ArrayList<String> finalListFiles = parsingFiles.parse(browser.getMovieFileNames());
        DbHandler dbHandler = new DbHandler(cacheDb,finalListFiles);
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
        // TODO: Fetch + display data according to the selected movie
        System.out.println("Fetching data for: " + movieListView.getSelectionModel().getSelectedItem());
    }
    
}
