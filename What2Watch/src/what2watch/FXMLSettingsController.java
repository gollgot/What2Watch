/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package what2watch;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void closeWindow(ActionEvent event) {
        // Getting a reference to the settings window window and closing it
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void setPath(ActionEvent event) {
    }

    @FXML
    private void browseFolders(ActionEvent event) {
    }
    
}
