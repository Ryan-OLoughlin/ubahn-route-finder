package com.example.UI;

import java.io.IOException;

import com.example.Model.Graph;
import com.example.Util.CSVLoader;

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
            // Initialize graph with data
            Graph graph = CSVLoader.loadGraph(
                "src/main/resources/com/example/station_locations.csv",
                "src/main/resources/com/example/vienna_subway.csv"
            );
            
            scene = new Scene(loadFXML("Main"), 2324, 1008);
            stage.setScene(scene);
            stage.setTitle("Vienna U-Bahn Route Finder");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
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
