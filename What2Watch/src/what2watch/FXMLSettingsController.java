/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Raphael.BAZZARI
 */
public class FXMLSettingsController implements Initializable {

    @FXML
    private Label lblInstruction;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnPathDefiner;
    @FXML
    private Button btnBrowse;
    @FXML
    private TextField tfPath;
    
    private static UserPreferences prefs = new UserPreferences();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Filling the textfield with the saved movie folder path
        if(! this.prefs.getPath().equals("")) {
            this.tfPath.setText(this.prefs.getPath());
        }
    }    

    /** 
     * Closes the settings window. This method uses the event that triggers it
     * in order to fetch the scene in which the event started and close the window.
     * 
     * @param event the event that triggers this method
     */
    @FXML
    private void closeWindow(ActionEvent event) {
        Node  source = (Node)  event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /** 
     * Saves the path specified in the settings window path textfield (tfPath) as a preferences.
     * This will ensure its persistence and make it available at anytime in the application.
     * 
     * @param event the event that triggers this method
     */
    @FXML
    private void setPath(ActionEvent event) {
        // Making sure the user specified a path for his movie folder
        if (!this.tfPath.getText().equals("")) {
            this.prefs.savePath(this.tfPath.getText());
            this.setLblErrorMode(false);
            
            closeWindow(event);
        } else {
            this.setLblErrorMode(true);
        }
    }

    /** 
     * Opens up a dialog window allowing the user to browse through the file system
     * and select a folder
     * 
     * @param event the event that triggers this method
     */
    @FXML
    private void browseFolders(ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage chooserStage = new Stage();
        
        configureDirectoryChooser(directoryChooser);
        
        File file = directoryChooser.showDialog(chooserStage);
        
        // Filling the path textfield if the user chose a directory
        if (file != null) {
            this.tfPath.setText(file.getAbsolutePath());
        }
    }
    
    /** 
     * Defines an initial folder to start browsing from as well as a window title.
     * If the user saved a path in the preferences, then this path is used as a the initial folder.
     * If no path has been saved, the directory chooser opens up the default user directory 
     * depending on the operating system the app is running on.
     * 
     * @param directoryChooser the DirectoryChooser instance to configure
     */
    private static void configureDirectoryChooser(final DirectoryChooser directoryChooser) {      
        String initialDirectory = System.getProperty("user.home");
        directoryChooser.setTitle("What 2 Watch - Select a directory");
        
        String movieFolderPath = prefs.getPath();
        
        // Sets the default folder opened by directory chooser to be the one saved by the user
        if(!movieFolderPath.equals("")) { 
            if (Files.exists(Paths.get(movieFolderPath))) {
                initialDirectory = movieFolderPath;
            }
        }
        
        directoryChooser.setInitialDirectory(new File(initialDirectory));
    }
    
    /** 
     * Toggles the instruction label of the settings window between two modes.
     * The normal mode displays a regular label indicating what the user should do.
     * The error mode updates the very same label with a red color and a text warning.
     * 
     * This method is called whenever the user clicks the save button prior to selecting a path.
     * 
     * @param errorMode the boolean value specifying whether or not the label should be displayed
     * in its error mode
     */
    private void setLblErrorMode(boolean errorMode) {
        if (!errorMode) {
            this.lblInstruction.setStyle("-fx-text-fill: #9c9c9c;");
            this.lblInstruction.setText("Select your movie directory");
        } else {
            this.lblInstruction.setStyle("-fx-text-fill: red;");
            this.lblInstruction.setText("Please select the folder containing your movies");
        } 
    }
}
