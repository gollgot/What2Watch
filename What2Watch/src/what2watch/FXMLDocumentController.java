/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import com.sun.org.apache.bcel.internal.generic.F2D;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Raphael.BAZZARI
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Button btnSettings;
    @FXML
    private TextArea taSynopsis;
    @FXML
    private ListView<String> listMovie;
    @FXML
    private TextField tfSearch;
    @FXML
    private ComboBox<String> cbxSearchCriterias;
    @FXML
    private ImageView ivMovie;
    @FXML
    private TextField tfStartingYear;
    @FXML
    private TextField tfEndingYear;
    @FXML
    private Label lblGenreValue;
    @FXML
    private Label lblActors;
    @FXML
    private Label lblActorsValue;
    @FXML
    private Label lblStartingYear;
    @FXML
    private Label lblEndingYear;
    @FXML
    private Label labelDirectors;
    @FXML
    private Label lblDirectorsValue;
    @FXML
    private Pane paneBlackOpacity;
    @FXML
    private ImageView imageViewBigPoster;
    @FXML
    private Text txtTitle;
    @FXML
    private Text txtYear;
    @FXML
    private Button btnRefresh;
    @FXML
    private Label lblPlay;
    @FXML
    private ImageView ivConnectionStatus;
    @FXML
    private ProgressBar progressBarProcess;
    @FXML
    private Label lblNbFilesProcessed;

    private UserPreferences prefs = new UserPreferences();
    private int activeSearchMode = 0;
    public static boolean exit = false; // Change if we close the application (see -> Main class)

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Displaying the settings window before the main one if no path has been saved in the app
        String movieFolderPath = this.prefs.getPath();
        if (movieFolderPath.equals("")) {
            try {
                showSettings(null);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (!Files.exists(Paths.get(movieFolderPath))) {
            displayMissingFolderError();
        }

        // Combobox search criterias configuration
        // those values have to match the switch case statement in the updateSearchMode method below
        this.cbxSearchCriterias.getItems().addAll(
                "Title", // index 0
                "Genre", // index 1
                "Year", // index 2
                "Director", // index 3
                "Actor" // index 4
        );

        // Disabling the search bars to prevent searches from being processed
        // until the movie list is displayed in the listView
        this.disableSearchUI(true);

        // Hide all things related of the big poster
        paneBlackOpacity.setVisible(false);
        imageViewBigPoster.setVisible(false);

        progressBarProcess.setVisible(false);
        lblNbFilesProcessed.setVisible(false);

        checkInternetConnection();

    }

    @FXML
    private void showSettings(ActionEvent event) throws IOException {
        // Settings window creation
        Parent root = FXMLLoader.load(getClass().getResource("FXMLSettings.fxml"));
        Stage settingStage = new Stage();

        // Window customization
        settingStage.setResizable(false);
        settingStage.initModality(Modality.APPLICATION_MODAL);
        settingStage.setTitle("Movie directory selection");

        Scene scene = new Scene(root);
        settingStage.setScene(scene);

        settingStage.getIcons().add(new Image("what2watch/resources/images/W2W_Logo.png"));
        settingStage.showAndWait();
    }

    @FXML
    private void browseFiles(ActionEvent event) throws IOException {
        String movieFolderPath = this.prefs.getPath();
        if (Files.exists(Paths.get(movieFolderPath))) {
            FileBrowser.getMovieFileInfos();
            
            CacheDb cacheDb = new CacheDb();
            // Update the cache db (add or remove movies on DB, it depend on the
            // browser.getMovieFileNames) and get real titles of all the movies on 
            // the DB.
            ArrayList<String> rawFileNames = FileBrowser.getMovieFileNames();
            ArrayList<String> cleanFileNames = ParsingFiles.parse(rawFileNames);
            
            DbHandler dbHandler = new DbHandler(cacheDb, cleanFileNames, rawFileNames);
            listMovie.getItems().clear();
            // Init progress indicator to 0 and display it
            progressBarProcess.setProgress(0);
            progressBarProcess.setVisible(true);

            this.disableSearchUI(true);
            // We pass the current instance of "FXMLDocumentController" class, because we have to access the "disableSearchUI" method 
            dbHandler.update(this, listMovie, progressBarProcess, lblNbFilesProcessed);
        } else {
            displayMissingFolderError();
        }
    }

    @FXML
    private void updateSearchMode(ActionEvent event) {
        int boxIndex = this.cbxSearchCriterias.getSelectionModel().getSelectedIndex();

        // NOTE: The combobox item values have to be defined so that they match the following statement
        switch (boxIndex) {
            case 0: // Title
                setYearSearchMode(false);
                this.tfSearch.requestFocus();
                this.activeSearchMode = 0;
                this.tfSearch.setPromptText("e.g. Star Wars, Titanic, ...");
                break;
            case 1: // Genre
                setYearSearchMode(false);
                this.tfSearch.requestFocus();
                this.activeSearchMode = 1;
                this.tfSearch.setPromptText("e.g. Drama, Action, ...");
                break;
            case 2: // Year
                setYearSearchMode(true);
                this.tfStartingYear.requestFocus();
                this.activeSearchMode = 2;
                break;
            case 3: // Director
                setYearSearchMode(false);
                this.tfSearch.requestFocus();
                this.activeSearchMode = 3;
                this.tfSearch.setPromptText("e.g. George Lucas, Guillermo del Toro, ...");
                break;
            case 4: // Actor
                setYearSearchMode(false);
                this.tfSearch.requestFocus();
                this.activeSearchMode = 4;
                this.tfSearch.setPromptText("e.g. Leonardo DiCaprio, Mila Kunis, ...");
                break;
            default:
                break;
        }

        this.searchForMatchingMovies(null);
    }

    // Disables search textfields and toggles the searchIsEnabled property
    public void disableSearchBars(boolean toggleValue) {
        this.tfSearch.setDisable(toggleValue);
        this.tfStartingYear.setDisable(toggleValue);
        this.tfEndingYear.setDisable(toggleValue);
    }

    // Displays/hides textfields according to the selected combobox search criteria
    private void setYearSearchMode(boolean on) {
        lblStartingYear.setVisible(on);
        tfStartingYear.setVisible(on);
        lblEndingYear.setVisible(on);
        tfEndingYear.setVisible(on);
        tfSearch.setVisible(!on);
    }

    // Clic on an items on the list
    @FXML
    private void getMovieInformations() {
        String movieTitle = listMovie.getSelectionModel().getSelectedItem();
        if (movieTitle != null) {
            Movie selectedMovie = DbHandler.getMovie(movieTitle);

            // getActors / Directors / Genres, return an array, so we have to format that
            // to have a String with a comma for separate data Ex : data1, data2, data3
            String actors = "";
            for (int i = 0; i < selectedMovie.getActors().length; i++) {
                actors += selectedMovie.getActors()[i] + ", ";
            }
            actors = actors.replaceAll(", $", ""); // Delete last comma

            String genres = "";
            for (int i = 0; i < selectedMovie.getGenre().length; i++) {
                genres += selectedMovie.getGenre()[i] + ", ";
            }
            genres = genres.replaceAll(", $", "");

            String directors = "";
            for (int i = 0; i < selectedMovie.getDirector().length; i++) {
                directors += selectedMovie.getDirector()[i] + ", ";
            }
            directors = directors.replaceAll(", $", "");

            // Set texts on the labels
            txtTitle.setText(selectedMovie.getTitle());
            txtYear.setText(" (" + selectedMovie.getYear() + ")");
            taSynopsis.setText(selectedMovie.getSynopsis());
            lblActorsValue.setText(actors);
            lblGenreValue.setText(genres);
            lblDirectorsValue.setText(directors);
            
            // Movie poster handling
            Image moviePoster = new Image("what2watch/resources/images/placeHolder.png");
            if (!selectedMovie.getPoster().equals("Unknown") && InternetConnection.isEnable()) {
                moviePoster = new Image("http://image.tmdb.org/t/p/w300" + selectedMovie.getPoster());
            }
            ivMovie.setImage(moviePoster);
            imageViewBigPoster.setImage(moviePoster);
        }
    }

    @FXML
    private void searchForMatchingMovies(KeyEvent event) {
        // Calling the right search methods according to the active search mode
        switch (activeSearchMode) {
            case 0: // Title
                SearchHandler.findMovieByTitle(this.tfSearch.getText());
                break;
            case 1: // Genre
                SearchHandler.findMovieByGenre(this.tfSearch.getText());
                break;
            case 2: // Year
                SearchHandler.findMovieByYearRange(this.tfStartingYear.getText(), this.tfEndingYear.getText());
                break;
            case 3: // Director
                SearchHandler.findMovieByDirector(this.tfSearch.getText());
                break;
            case 4: // Actor
                SearchHandler.findMovieByActor(this.tfSearch.getText());
                break;
            default:
                break;
        }
    }

    @FXML
    private void updateDisplayedInfos(KeyEvent event) {
        if (event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.DOWN)) {
            getMovieInformations();
        }
    }

    @FXML
    private void displayBigPoster(MouseEvent event) {
        paneBlackOpacity.setVisible(true);
        imageViewBigPoster.setVisible(true);
        // We set the focus to the BigPoster (like that we can check if we presse the escape key or not)
        imageViewBigPoster.requestFocus();
    }

    private void closeBigPoster() {
        paneBlackOpacity.setVisible(false);
        imageViewBigPoster.setVisible(false);
    }

    // Close the bigPoster when we clicked on the black opac pane
    @FXML
    private void paneBlackOpacityClicked(MouseEvent event) {
        closeBigPoster();
    }

    // Close the bigPoster when we presse escape key
    @FXML
    private void bigPosterKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            closeBigPoster();
        }
    }

    public void disableSearchUI(boolean toggleValue) {
        this.disableSearchBars(toggleValue);
        this.cbxSearchCriterias.setDisable(toggleValue);
        this.listMovie.setDisable(toggleValue);
    }

    @FXML
    private void imgPlayerClicked(MouseEvent event) {
        String title = txtTitle.getText();
        String rawTitle = DbHandler.getRawTitle(title);
        String path = FileBrowser.getFilePath(rawTitle);
        String os = System.getProperty("os.name").toLowerCase();
        
        if(isMac(os) || isWindows(os)){
            // Check if the Operating system can use Desktop open action or not
            if(Desktop.isDesktopSupported()){
                // Desktop open is for open the file with the linked application launcher on the OS
                File movieFile = new File(path);
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(movieFile);
                } catch (IOException ex) {
                    System.out.println("Error in 'imgPlayerClicked' method in 'FXMLDocumentController' classe. EX:" + ex.getMessage().toString());

                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("This file cannot be read");
                    alert.setContentText("No program handling this type of file has been found on your system");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    });
                }
            }else{
                showErrorOS();
            }
        }else{
            showErrorOS();
        }
    }
    
    private boolean isWindows(String os) {
        return (os.indexOf("win") >= 0);
    }

    private boolean isMac(String os) {
        return (os.indexOf("mac") >= 0);
    }
    
    private void showErrorOS(){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("What 2 Watch cannot open this movie file");
        alert.setContentText("Your operating system doesn't allow What 2 Watch to open this movie file. Please open it manually.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                alert.close();
            }
        });
    }

    private void checkInternetConnection() {

        Thread checkInternetConnection = new Thread(new Runnable() {
            @Override
            public void run() {
                // Exit changeed  if we close the application (look at Main class), this way we can close the thread
                while (exit == false) {
                    try {
                        if (InternetConnection.isEnable()) {
                            ivConnectionStatus.setStyle("-fx-background-color: null; -fx-image: url(\"what2watch/resources/images/greenDot.png\")");
                        } else {
                            ivConnectionStatus.setStyle("-fx-background-color: null; -fx-image: url(\"what2watch/resources/images/redDot.png\")");
                        }
                        
                        // Check every second
                        Thread.sleep(1000);

                    } catch (InterruptedException ex) {
                        System.out.println("Error on checkInternetConnection method in FXMLDocumentCOntroller class. Ex: " + ex.getMessage().toString());
                    }
                }
            }
        });

        checkInternetConnection.start();

    }

    private void toggleHoveredIcon(MouseEvent event, String iconSuffix) {
        String elementInfos = event.getSource().toString();
        String elementId = elementInfos.substring(elementInfos.indexOf("id=") + 3, elementInfos.indexOf(","));
        Node hoveredElement = (Node) event.getSource();
        hoveredElement.setStyle("-fx-background-color: null; -fx-graphic: url(\"what2watch/resources/images/" + elementId + iconSuffix + ".png\")");
    }

    @FXML
    private void disableHoveredIcon(MouseEvent event) {
        String iconSuffix = "";
        toggleHoveredIcon(event, iconSuffix);
    }

    @FXML
    private void enableHoveredIcon(MouseEvent event) {
        String iconSuffix = "Hovered";
        toggleHoveredIcon(event, iconSuffix);
    }
    
    private void displayMissingFolderError() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("What 2 Watch - Error");
        alert.setHeaderText("Missing movie folder");
        alert.setContentText("The folder containing your movies has been moved or deleted.\n"
                + "Please select the folder containing your movies.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                alert.close();
                try {
                    showSettings(null);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
