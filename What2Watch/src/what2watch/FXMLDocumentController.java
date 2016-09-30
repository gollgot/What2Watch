/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    
    private UserPreferences prefs = new UserPreferences();
    private FileBrowser browser = new FileBrowser();
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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
        String path = this.prefs.getPath();
        browser.fetchMoviesFileNames(path);
    }
    
}
