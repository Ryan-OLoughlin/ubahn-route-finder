package com.example.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import com.example.Util.CSVLoader;
import java.util.List;

public class RouteController {

    // change colours of map to repereesent different lines on route.
    // display list of stations on route
    // display cost of route (distance or price)

    @FXML
    private ComboBox<String> startStationComboBox;

    @FXML
    private ComboBox<String> destinationStationComboBox;

    @FXML
    public void initialize() {
        // Load station names from CSV 
        List<String> stations = CSVLoader.loadStationNames();
        startStationComboBox.getItems().addAll(stations);
        destinationStationComboBox.getItems().addAll(stations);
    }

    @FXML
    public void onFindRoute() {
        // Implementation logic for finding the route
        System.out.println("Find Route action triggered");
    }

    @FXML
    public void onClear() {
        // Logic to clear inputs and outputs
        System.out.println("Clear action triggered");
    }

}
