package com.example.Controller;
import java.util.List;

import com.example.Model.Graph;
import com.example.Model.GraphNodeAL;
import com.example.Util.CSVLoader;
import com.example.Util.GraphAlgorithms;
import com.example.Util.GraphAlgorithms.CostedPath;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class RouteController {

    // change colours of map to repereesent different lines on route.
    // display list of stations on route
    // display cost of route (distance or price)

    private Graph graph;

    @FXML
    private Canvas mapCanvas;

    @FXML
    private TextArea routeOutput;

    @FXML
    private ComboBox<String> startStationComboBox;

    @FXML
    private ComboBox<String> destinationStationComboBox;

    private MapController mapController;

    @FXML
    public void initialize() {
        // Load station names from CSV 
        List<String> stations = CSVLoader.loadStationNames();
        startStationComboBox.getItems().addAll(stations);
        destinationStationComboBox.getItems().addAll(stations);
        startStationComboBox.setValue("Select Start Station");
        destinationStationComboBox.setValue("Select Destination Station");
        mapController = new MapController();
        mapController.setCanvas(mapCanvas);

    }

    public MapController getMapController() {
        return mapController;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    
        // DEBUG: Print the graph structure to console
        System.out.println("=== Loaded Graph ===");
        for (GraphNodeAL<?> node : graph.getNodeList()) {
            System.out.println("Station: " + node.getName());
            for (var link : node.getAdjList()) {
                System.out.println("  connects to → " + link.destNode.getName() + " (cost: " + link.cost + ")");
            }
        }
    }
    @FXML
    public void onFindRoute() {
        String start = startStationComboBox.getValue().trim();
        String end = destinationStationComboBox.getValue().trim();

        if (start == null || end == null || graph == null){
            routeOutput.setText("Please select both start and destination stations");
            return;
        }

        GraphNodeAL<?> startNode = graph.findStation(start);
        GraphNodeAL<?> endNode = graph.findStation(end);

        if ( startNode == null || endNode == null) {
            routeOutput.setText("Selected station(s) not found in graph");
            return;
        }

        CostedPath bfsPath = GraphAlgorithms.findAnyRouteBFS(startNode, end);

        if ( bfsPath == null ) {
            routeOutput.setText("No route found between selected stations.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Route found(").append(bfsPath.pathList.size() - 1).append(" stops):\n");
            for (GraphNodeAL<?> node : bfsPath.pathList) {
                sb.append("→ ").append(node.getName()).append("\n");
            }
            sb.append("\nTotal Distance: ").append(bfsPath.pathCost).append(" km");
            routeOutput.setText(sb.toString());

            if ( mapController != null ) {
                mapController.drawRoute(bfsPath.pathList);
            }
        }
    }

    @FXML
    public void onFindAllRoutes(){
        String start = startStationComboBox.getValue().trim();
        String end = destinationStationComboBox.getValue().trim();

        if ( start == null || end == null ) {
            routeOutput.setText("Please select both stations.");
            return;
        }
        GraphNodeAL<?> startNode = graph.findStation(start);
        List<GraphAlgorithms.CostedPath> allPaths = GraphAlgorithms.findAllDFSPaths(startNode, null, 0, end);

        if ( allPaths.isEmpty() ) {
            routeOutput.setText("No routes found.");
            return;
        }

        StringBuilder sb = new StringBuilder("DFS: All route permutations\n");

        CostedPath shortest = allPaths.get(0);
        for (int i = 0; i < allPaths.size(); i++) {
            GraphAlgorithms.CostedPath path = allPaths.get(i);
            sb.append("Route ").append(i + 1).append(" (").append(path.pathCost).append(" km):\n");
            for (GraphNodeAL<?> node : path.pathList) {
                sb.append("→ ").append(node.getName()).append("\n");
            }
            sb.append("\n");
        }
        routeOutput.setText(sb.toString());

        if ( mapController != null ) {
            mapController.drawRoute(shortest.pathList);
        }
    }

    @FXML
    public void onFindShortestRoute(){
        String start = startStationComboBox.getValue().trim();
        String end = destinationStationComboBox.getValue().trim();

        if ( start == null || end == null ) {
            routeOutput.setText("Please select both stations.");
            return;
        }

        GraphNodeAL<?> startNode = graph.findStation(start);
        GraphAlgorithms.CostedPath path = GraphAlgorithms.findCheapestPathDijkstra(startNode, end);

        if ( path == null ) {
            routeOutput.setText("No shortest route found.");
            return;
        }

        StringBuilder sb = new StringBuilder("Dijkstra: Shortest Route\n");
        for(GraphNodeAL<?> node : path.pathList) {
            sb.append("→ ").append(node.getName()).append("\n");
        }
        sb.append("\nTotal Distance: ").append(path.pathCost).append(" km");
        routeOutput.setText(sb.toString());

        if ( mapController != null ) {
            mapController.drawRoute(path.pathList);
        }
    }

    @FXML
    public void onFindRouteWithPenalty() {
        String start = startStationComboBox.getValue().trim();
        String end = destinationStationComboBox.getValue().trim();
        int penalty = 10;

        if ( start == null || end == null ) {
            routeOutput.setText("Please select both stations.");
            return;
        }

        GraphNodeAL<?> startNode = graph.findStation(start);
        GraphAlgorithms.CostedPath path = GraphAlgorithms.findCheapestPathWithPenalty(startNode, end, penalty);

        if ( path == null ) {
            routeOutput.setText("No route found with penalty.");
            return;
        }

        StringBuilder sb = new StringBuilder("Dijkstra + Penalty (Penalty: ").append(penalty).append(")\n");
        for (GraphNodeAL<?> node : path.pathList) {
            sb.append("→ ").append(node.getName()).append("\n");
        }
        sb.append("\nTotal Cost: ").append(path.pathCost).append(" km");
        routeOutput.setText(sb.toString());

        if ( mapController != null ) {
            mapController.drawRoute(path.pathList);
        }
    }

    public void setMapController(MapController mapController) {
        this.mapController = mapController;
    }

    @FXML
    public void onClear() {
        // Logic to clear inputs and outputs
        System.out.println("Clear action triggered");
    }

}
