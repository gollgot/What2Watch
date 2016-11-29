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
    private Label instructionLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private Button pathButton;
    @FXML
    private Button browseButton;
    @FXML
    private TextField pathTextField;
    @FXML
    private Label emptyPathErrorLabel;
    
    private static UserPreferences prefs = new UserPreferences();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // For test purposes only 
        // this.prefs.removePath();

        // Filling the textfield with the saved movie folder path
        if(! this.prefs.getPath().equals("")) {
            this.pathTextField.setText(this.prefs.getPath());
        }
    }    

    @FXML
    private void closeWindow(ActionEvent event) {
        closeStage(cancelButton);
    }

    @FXML
    private void setPath(ActionEvent event) {
        // Making sure the user specified a path for his movie folder
        if (!this.pathTextField.getText().equals("")) {
            this.prefs.savePath(this.pathTextField.getText());
            this.emptyPathErrorLabel.setVisible(false);
            
            // Closing the settings window
            closeStage(pathButton);
        } else {
            this.emptyPathErrorLabel.setVisible(true);
        }
    }

    @FXML
    private void browseFolders(ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage chooserStage = new Stage();
        
        configureDirectoryChooser(directoryChooser);
        
        File file = directoryChooser.showDialog(chooserStage);
        
        // Filling the path textfield if the user chose a directory
        if (file != null) {
            this.pathTextField.setText(file.getAbsolutePath());
        }
    }
    
    private static void configureDirectoryChooser(final DirectoryChooser directoryChooser) {      
        String initialDirectory = System.getProperty("user.home");
        directoryChooser.setTitle("Select directory");
        
        String movieFolderPath = prefs.getPath();
        
        // Sets the default folder opened by directory chooser to be the one saved by the user
        if(!movieFolderPath.equals("")) { 
            if (Files.exists(Paths.get(movieFolderPath))) {
                initialDirectory = movieFolderPath;
            }
        }
        
        directoryChooser.setInitialDirectory(new File(initialDirectory));
    }
    
    private static void closeStage(Button button) {
        // Getting a reference to the settings window and closing it
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}
