package com.example.UI;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class MainApp extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) {
        try {
            // Adjust the size as needed or use preferred dimensions and confirm the FXML file name
            scene = new Scene(loadFXML("Main"), 640, 480);
            stage.setScene(scene);
            stage.setTitle("Vienna U-Bahn Route Finder");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();  // Print the stack trace to diagnose initialization errors
        }
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/com/example/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
