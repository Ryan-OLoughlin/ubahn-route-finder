package com.example.UI;

import com.example.Controller.MapController;
import com.example.Controller.RouteController;
import com.example.Model.Graph;
import com.example.Util.CSVLoader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            System.out.println(" App launched");

            // Debug: Print classpath loading
            String stationCsvPath = getClass().getResource("/com/example/station_locations.csv").getPath();
            String linkCsvPath = getClass().getResource("/com/example/vienna_subway.csv").getPath();
            System.out.println(" Station CSV: " + stationCsvPath);
            System.out.println(" Link CSV: " + linkCsvPath);

            Graph graph = CSVLoader.loadGraph(stationCsvPath, linkCsvPath);
            System.out.println(" Graph loaded");

            // Load Main.fxml
            System.out.println(" Loading Main.fxml...");
            FXMLLoader routeLoader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
            Parent routeRoot = routeLoader.load();
            RouteController routeController = routeLoader.getController();
            System.out.println(" RouteController loaded");

            // Load MapView.fxml
            System.out.println(" Loading MapView.fxml...");
            FXMLLoader mapLoader = new FXMLLoader(getClass().getResource("/fxml/MapView.fxml"));
            Parent mapRoot = mapLoader.load();
            MapController mapController = mapLoader.getController();
            System.out.println(" MapController loaded");

            // Inject dependencies
            routeController.setGraph(graph);
            routeController.setMapController(mapController);
            mapController.setGraph(graph);
            System.out.println(" Dependencies injected");

            // Combine and show
            VBox root = new VBox(routeRoot, mapRoot);
            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPannable(true);

            Scene scene = new Scene(scrollPane, 1280, 720);
            stage.setScene(scene);
            stage.setTitle("Vienna U-Bahn Route Finder");
            stage.show();

            System.out.println(" App ready");

        } catch (Exception e) {
            System.out.println(" ERROR during app startup:");
            e.printStackTrace(); // This will point to any FXML parsing or controller binding issues
        }
    }

    public static void main(String[] args) {
        launch();
    }
}

