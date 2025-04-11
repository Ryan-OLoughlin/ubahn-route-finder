package com.example.UI;

import com.example.Controller.MapController;
import com.example.Controller.RouteController;
import com.example.Model.Graph;
import com.example.Util.CSVLoader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Load the graph data
            Graph graph = CSVLoader.loadGraph(
                    "src/main/resources/com/example/station_locations.csv",
                    "src/main/resources/com/example/vienna_subway.csv"
            );

            // Load Main.fxml (UI with routing controls)
            FXMLLoader routeLoader = new FXMLLoader(getClass().getResource("/com/example/Main.fxml"));
            Parent routeRoot = routeLoader.load();
            RouteController routeController = routeLoader.getController();

            // Load MapView.fxml (Canvas for drawing the map)
            FXMLLoader mapLoader = new FXMLLoader(getClass().getResource("/com/example/MapView.fxml"));
            Parent mapRoot = mapLoader.load();
            MapController mapController = mapLoader.getController();

            // Inject dependencies
            routeController.setGraph(graph);
            routeController.setMapController(mapController);
            mapController.setGraph(graph);

            // Combine both views into a VBox
            VBox root = new VBox(routeRoot, mapRoot);

            // Set and show the scene
            Scene scene = new Scene(root, 2324, 1008);
            stage.setScene(scene);
            stage.setTitle("Vienna U-Bahn Route Finder");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
