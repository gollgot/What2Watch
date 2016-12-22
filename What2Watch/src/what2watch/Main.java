/*
 * This is the Main class, used for init the FXML Stage, Scene, etc.
 * And for create the Database if it doesn't already exists
 */
package what2watch;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author Raphael.BAZZARI and Loïc Dessaules
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("FXMLDocument.fxml"));
        Parent root = loader.load();
        
        FXMLDocumentController controller = loader.getController();
        
        Scene scene = new Scene(root);
        
        Font.loadFont(getClass().getResourceAsStream("resources/fonts/SourceSansPro-Regular.otf"), 12);
        Font.loadFont(getClass().getResourceAsStream("resources/fonts/Montserrat-Bold.ttf"), 12);
        scene.getStylesheets().add("what2watch/default.css");
        
        stage.setOnShown(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent e) {
                controller.viewDidLoad();
            }
        });
        
        // StageStyle.UNIFIED is for remove the basic blue border of the windows
        stage.initStyle(StageStyle.UNIFIED);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("What 2 Watch");
        stage.getIcons().add(new Image("what2watch/resources/images/W2W_Logo.png"));
        stage.show();
        
        // When we close the application, we assign true to the exit variable on the controler. 
        // This way, the controller class know we exit the programm, then, we can close all thread 
        // This case, the thread who check the internet connection. 
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() { 
            @Override 
            public void handle(WindowEvent event) { 
                // We close the programm 
                FXMLDocumentController.exit = true; 
            } 
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Before all things, we create the DataBase if it doesn't exists
        CacheDb cacheDb = new CacheDb();
        if(!cacheDb.exists()) {
            cacheDb.create();
        }else{
            System.out.println("The cache file already exists.");
        }

        // After : Launch the window
        launch(args);
    }
    
}
