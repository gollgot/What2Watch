/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

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
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import static javafx.scene.input.MouseEvent.MOUSE_ENTERED;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    @FXML
    private GridPane gridPane;
    @FXML
    private VBox vbxLeftContainer;
    @FXML
    private Label lblSearchBy;

    private UserPreferences prefs = new UserPreferences();
    private int activeSearchMode = 0;
    public static boolean exit = false; // Change if we close the application (see -> Main class)
    private ImageView instructionHolder;
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        
        // Display the instruction picture in the UI and sets up "this.instructionHolder" for later use
        initInstructionPicture();
    }

    
    /**
     * Opens up the settings window after setting up its title and icon.
     *
     * This method displays the settings window modal mode.
     * 
     * @param event the event that triggers this method
     */
    @FXML
    private void showSettings(ActionEvent event) throws IOException {
        // Settings window creation
        Parent root = FXMLLoader.load(getClass().getResource("FXMLSettings.fxml"));
        Stage settingStage = new Stage();

        // Window customization
        settingStage.setResizable(false);
        settingStage.initModality(Modality.APPLICATION_MODAL);
        settingStage.setTitle("What 2 Watch - Movie directory selection");
        
        Scene scene = new Scene(root);
        Font.loadFont(getClass().getResourceAsStream("resources/fonts/SourceSansPro-Regular.otf"), 12);
        scene.getStylesheets().add("what2watch/default.css");
        settingStage.setScene(scene);

        settingStage.getIcons().add(new Image("what2watch/resources/images/W2W_Logo.png"));
        settingStage.showAndWait();
    }

    
    /**
     * This method is responsible for initiating the movie folder scan process and
     * manage the UI appearance accordingly. Once the movie folder scan is started,
     * this method displays a progressbar indicating the scan progression. It also disables
     *the refresh button to prevent multiple scans from being triggered at
     * once. Other UI elements such as the search textfield and the movie listView are
     * disabled when a scan is occuring. When launching a scan, if the folder specified 
     * by the path saved in the preferences no longer exists, an error dialog is displayed to the user.
     *
     * @param event the event that triggers this method
     */
    @FXML
    private void browseFiles(ActionEvent event) throws IOException {
        String movieFolderPath = this.prefs.getPath();
        if (Files.exists(Paths.get(movieFolderPath))) {
            FileBrowser.getMovieFileInfos();
        displayMovieInfos(false);
        CacheDb cacheDb = new CacheDb();
        // Update the cache db (add or remove movies on DB, it depend on the
        // browser.getMovieFileNames) and get real titles of all the movies on 
        // the DB.
        ArrayList<String> fileNames = ParsingFiles.parse(FileBrowser.getMovieFileNames());
        ArrayList<String> rawFileNames = FileBrowser.getMovieFileNames();
        DbHandler dbHandler = new DbHandler(cacheDb, fileNames, rawFileNames);
        listMovie.getItems().clear();
        // Init progress indicator to 0 and display it
        progressBarProcess.setProgress(0);
        progressBarProcess.setVisible(true);

            this.disableRefreshButton(true);
            this.disableSearchUI(true);
            
            // We pass the current instance of "FXMLDocumentController" class, because we have to access the "disableSearchUI" method 
            dbHandler.update(this, listMovie, progressBarProcess, lblNbFilesProcessed);
        } else {
            displayMissingFolderError();
        }
    }

    /**
    * This method is only invoked when the user choses a value from the search criteria combobox (cbxSearchCriterias).
    * It is responsible for drawing / hiding UI components according to the active search mode.
    * For instance. A different textfield placeholder is displayed depending on the value chosen from
    * the search criteria combobox. Same goes with the different textfields used to search for a movie i.e. some may only be visible 
    * when a certain search mode is active. Furthermore, the focus is always "passed" to the main textfield of the active search mode.
    * In addition, a search will be triggered with the value of the active textfield(s) each time a combobox element is selected.
    *
    * @param event the event that triggers this method
    * 
    * @see FXMLDocumentController#setYearSearchMode
    * @see FXMLDocumentController#searchForMatchingMovies
    */
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

    /**
    * Disables search textfields depending on the value passed by parameter.
    * This method is used within FXMLDocumentController.disableSearchUI.
    *
    * @param toggleValue boolean value indicating whether or not an element should be disabled
    * 
    * @see FXMLDocumentController#disableSearchUI
    */
    public void disableSearchBars(boolean toggleValue) {
        this.tfSearch.setDisable(toggleValue);
        this.tfStartingYear.setDisable(toggleValue);
        this.tfEndingYear.setDisable(toggleValue);
    }

    /**
    * Displays / hides textfields depending on the value passed by parameter.
    * When the year mode is set to true, the UI only displays two labels and two textfields
    * allowing the user to search for a movie by indicating year range. 
    * When the year mode is set to false, only the basic search textfield is displayed.
    * 
    * This method is called in FLXMDocumentController.updateSearchMode
    *
    * @param on boolean value indicating whether or not an element should be visible
    * 
    * @see FXMLDocumentController#updateSearchMode
    */
    private void setYearSearchMode(boolean on) {
        lblStartingYear.setVisible(on);
        tfStartingYear.setVisible(on);
        lblEndingYear.setVisible(on);
        tfEndingYear.setVisible(on);
        tfSearch.setVisible(!on);
    }

    /**
    * This method is responsible for reacting to click events occuring on the movie list view
    * from the main window. When a click event occurs on a cell of the movie list, the title of the
    * movie is fetched and used to retrieve all informations related to this movie. Those informations
    * are then displayed using the labels and other UI components of the main window.
    * 
    * Movie posters are also fetched from the web service when a click occurs.
    * Calling this method will always hide the instruction picture displayed in the main window
    */
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
            
            // Hide the instruction picture ; display the movie infos
            displayMovieInfos(true);
        }
    }

    
    /**
    * Dispatches search terms contained in search textfields to the SearchHandler class depending on the active search mode.
    * SearchHandler then processes the search using the search terms it recieves. For instance, if the active search mode 
    * is "searches by actor", this method passes the search term to the right method within the SearchHandler class.
    * 
    * @param event the event that triggers this method
    * 
    * @see SearchHandler#findMovieByTitle
    * @see SearchHandler#findMovieByGenre
    * @see SearchHandler#findMovieByYearRange
    * @see SearchHandler#findMovieByDirector
    * @see SearchHandler#findMovieByActor
    */
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

    /**
    * Calls FXMLDocumentController.getMovieInformations in order update the informations displayed 
    * in the movie listView upon receiving keyEvents from either the UP_KEY or the DOWN_KEY
    * 
    * @param event the event that triggers this method
    * 
    * @see FXMLDocumentController#getMovieInformations
    */
    @FXML
    private void updateDisplayedInfos(KeyEvent event) {
        if (event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.DOWN)) {
            getMovieInformations();
        }
    }

    /**
    * Animates the appearance of the big movie poster with a fade in effect upon receiving click events
    * from the poster thumbnail in the main window.
    * 
    * This method should be used along with FXMLDocumentController.closeBigPoster in order
    * to manage the big poster visibility.
    * 
    * @param event the event that triggers this method
    * 
    * @see FXMLDocumentController#closeBigPoster
    */
    @FXML
    private void displayBigPoster(MouseEvent event) {
        paneBlackOpacity.setVisible(true);
        imageViewBigPoster.setVisible(true);
        // We set the focus to the BigPoster (like that we can check if we presse the escape key or not)
        imageViewBigPoster.requestFocus();
        
        FadeTransition ft = new FadeTransition(Duration.millis(300), paneBlackOpacity);
        ft.setFromValue(0.0);
        ft.setToValue(0.5);
        ft.setAutoReverse(true);
        
        FadeTransition ft2 = new FadeTransition(Duration.millis(300), imageViewBigPoster);
        ft2.setFromValue(0.0);
        ft2.setToValue(1.0);
        ft2.setAutoReverse(true);

        ParallelTransition pt = new ParallelTransition(ft, ft2);
        pt.play();
    }

    /**
    * Animates the disappearance of the big movie poster with a fade out effect upon receiving click events
    * from the black background pane surrounding the big poster.
    * 
    * This method should be used along with FXMLDocumentController.displayBigPoster in order
    * to manage the big poster visibility.
    * 
    * @see FXMLDocumentController#displayBigPoster
    */
    @FXML
    private void closeBigPoster() {
        paneBlackOpacity.setVisible(true);
        imageViewBigPoster.setVisible(true);
        // We set the focus to the BigPoster (like that we can check if we presse the escape key or not)
        imageViewBigPoster.requestFocus();
        
        FadeTransition ft = new FadeTransition(Duration.millis(300), paneBlackOpacity);
        ft.setFromValue(0.5);
        ft.setToValue(0.0);
        ft.setAutoReverse(true);
        
        FadeTransition ft2 = new FadeTransition(Duration.millis(300), imageViewBigPoster);
        ft2.setFromValue(1.0);
        ft2.setToValue(0.0);
        ft2.setAutoReverse(true);
        
        ParallelTransition pt = new ParallelTransition(ft, ft2);
        
        pt.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                paneBlackOpacity.setVisible(false);
                imageViewBigPoster.setVisible(false);
            }
        });
        
        pt.play();
    }

    /**
    * Calls FXMLDocumentController.closeBigPoster in order hide the big poster from the main
    * window upon receiving keyEvents from ESCAPE_KEY
    * 
    * @param event the event that triggers this method
    * 
    * @see FXMLDocumentController#closeBigPoster
    */
    @FXML
    private void bigPosterKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            closeBigPoster();
        }
    }

    /**
    * Disables UI components that should not be used while a scan is in progress according to the passed parameter.
    * Such elements include the search textfields, the search criteria combobox, the movie listView
    * as well as the "search By" label.
    * 
    * This method is called from many different places including FXMLDocumentController.initialize,
    * FXMLDocumentController.browseFiles and DbHandler.update.
    * 
    * @param toggleValue the boolean value indicating whether the UI components should be disabled or not
    * 
    * @see FXMLDocumentController#initialize
    * @see FXMLDocumentController#browseFiles
    * @see DbHandler#update
    */
    public void disableSearchUI(boolean toggleValue) {
        this.disableSearchBars(toggleValue);
        this.cbxSearchCriterias.setDisable(toggleValue);
        this.listMovie.setDisable(toggleValue);
        this.lblSearchBy.setDisable(toggleValue);
    }
    
    /**
    * Disables the refresh button to prevent users from triggering multiple folder scans at once.
    * This method is called within FXMLDocumentController.browseFiles and DbHandler.update
    * 
    * @param disabled the boolean value indicating whether the refresh button should be disabled or not
    * 
    * 
    * @see FXMLDocumentController#browseFiles
    * @see DbHandler#update
    */
    public void disableRefreshButton(boolean disabled) {
        if (!disabled) {
            // Forcing the button opacity to its original value to prevent unwanted effects
            this.btnRefresh.setOpacity(1.0);
        }
        this.btnRefresh.setDisable(disabled);
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

    /**
    * Applies icons to node elements depending on their states i.e. hovered or not.
    * For instance, if a Button is hovered, this method applies it the icon corresponding to the appearance the button should have once hovered.
    * 
    * To properly use this method, a node element HAS TO be set with a default picture named "elementid"."fileExtention". 
    * An other picture, the one that the element should display once hovered, should be stored in a resources folder using 
    * the following naming convention "elementidHovered"."fileExtension". This method will then handle the transition 
    * between the two icons depending on the element being hovered or not.
    * 
    * Here is an exemple. Consider a button with an id of "myButton". This button default picture should be named "myButton.png".
    * An other picture should be stored in a resource folder under the name of "myButtonHovered.png" 
    * (notice the presence of the "Hovered" keyword in the file name).
    * This method then sets the appropriate icon to the button according to its state (hovered or not)
    * 
    * Note that the element must be listenening for MOUSE_ENTERED and MOUSE_EXCITED events for this method to work properly
    * 
    * @param event the that triggers this method and helps it setting the right icon to the node element
    * 
    * @see MouseEvent#MOUSE_ENTERED
    * @see MouseEvent#MOUSE_EXITED
    */
    @FXML
    private void toggleHoveredIcon(MouseEvent event) {
        String iconSuffix = "";
        
        // This method only responds to MOUSE_ENTERED and MOUSE_EXCITED 
        // therefore, no need to check for the latter
        if (event.getEventType() == MOUSE_ENTERED) {
            iconSuffix = "Hovered";
        }
        
        String elementInfos = event.getSource().toString();
        String elementId = elementInfos.substring(elementInfos.indexOf("id=") + 3, elementInfos.indexOf(","));
        Node hoveredElement = (Node) event.getSource();
        hoveredElement.setStyle("-fx-background-color: null; -fx-graphic: url(\"what2watch/resources/images/" + elementId + iconSuffix + ".png\")");
    }
    
    /**
    * Displays an error popup letting the user know that the folder containing his / her
    * movies no longer exists or that it has been moved somewhere else in the file system.
    * 
    * The user is then lead to the settings window to chose a new folder upon 
    * clicking on the "OK" button of the popup.
    * 
    * This method is called within FXMLDocumentController.browseFiles and FXMLDocumentController.viewDidLoad
    * 
    * @see FXMLDocumentController#viewDidLoad
    * @see FXMLDocumentController#browseFiles
    */
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
    
    
    /**
    * Inserts the instruction picture into the scene graph so that it is ready to be used.
    * The instruction picture is loaded within FXMLDocumentController's property "this.instructionHolder"
    * and then placed into the main window gridPane.
    * 
    * Once placed into the gridPane, the UI components responsible for displaying movie informations
    * (labels, imageView etc.) are setVisible(false) to allow the instruction picture to fill up the space
    * 
    * This method is called within FXMLDocumentController.initialize
    * 
    * @see FXMLDocumentController#initialize
    */
    private void initInstructionPicture() {
        this.instructionHolder = new ImageView();

        Image instructions = new Image("what2watch/resources/images/instructions.png");
        this.instructionHolder.setImage(instructions);
        
        gridPane.add(this.instructionHolder, 1, 0, 1, 4);
        
        this.instructionHolder.setTranslateX(15);
        
        this.vbxLeftContainer.setVisible(false);
    }
    
    /**
    * Hides the UI movie informations components to draw the instruction picture instead. 
    * Does exactly the opposite depending on the value that's been passed by parameter.
    * 
    * This method is called within FXMLDocumentController.browseFiles and FXMLDocumentController.getMovieInformations
    * 
    * @param display the boolean value indicating whether or not UI components should be hid
    *
    * @see FXMLDocumentController#browseFiles
    * @see FXMLDocumentController#getMovieInformations
    */
    public void displayMovieInfos(boolean display) {
        this.vbxLeftContainer.setVisible(display);
        this.instructionHolder.setVisible(!display);
    }
    
    
    /**
    * This method is called right when the app is launched to handle the possibility
    * that the movie folder no longer exists or that it doesn't even exist at all 
    * if the app is launched for the first time. 
    * 
    * If the movie folder no longer exists, an error popup is presented using FXMLDocumentController.browseFiles.
    * If the app is launched for the very first time, the settings window is presented to the user to allow him / her
    * to define the path of his / her movie folder before using the app.
    * 
    * @see FXMLDocumentController#showSettings
    * @see FXMLDocumentController#displayMissingFolderError
    */
    public void viewDidLoad() {
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
    }
}
