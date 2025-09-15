package com.example.Controller;
import java.util.List;

import com.example.Model.Graph;
import com.example.Model.GraphNodeAL;
import com.example.Util.CSVLoader;
import com.example.Util.GraphAlgorithms;
import com.example.Util.GraphAlgorithms.CostedPath;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;

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
    
    @FXML
    private ListView<String> waypointsListView;

    @FXML
    private ListView<String> avoidStationsListView;

    @FXML
    private Slider penaltySlider;

    @FXML
    private Label penaltyLabel;

    private MapController mapController;

    @FXML
    public void initialize() {
        // Load station names from CSV 
        List<String> stations = CSVLoader.loadStationNames();
        startStationComboBox.getItems().addAll(stations);
        destinationStationComboBox.getItems().addAll(stations);
        startStationComboBox.setValue("Select Start Station");
        destinationStationComboBox.setValue("Select Destination Station");
        waypointsListView.getItems().addAll(stations);
        waypointsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        avoidStationsListView.getItems().addAll(stations);
        avoidStationsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        penaltySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            penaltyLabel.setText("Penalty for line changes: " + newValue.intValue());
        });
        

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
    List<String> waypointNames = waypointsListView.getSelectionModel().getSelectedItems();
    List<String> avoidStations = avoidStationsListView.getSelectionModel().getSelectedItems();

    if (start == null || end == null || graph == null) {
        routeOutput.setText("Please select both start and destination stations");
        return;
    }

    GraphNodeAL<?> startNode = graph.findStation(start);
    GraphNodeAL<?> endNode = graph.findStation(end);

    if (startNode == null || endNode == null) {
        routeOutput.setText("Selected station(s) not found in graph");
        return;
    }

    GraphAlgorithms.CostedPath path;

    if (waypointNames.isEmpty()) {
        // Simple BFS route with avoid logic
        path = GraphAlgorithms.findAnyRouteBFS(startNode, end, avoidStations);
    } else {
        // Build segmented BFS route through waypoints with avoid logic
        path = buildBFSPathWithWaypoints(startNode, endNode, waypointNames, avoidStations, graph);
    }

    if (path == null) {
        routeOutput.setText("No route found (possibly due to avoided stations).");
    } else {
        StringBuilder sb = new StringBuilder();
        sb.append("Route found (").append(path.pathList.size() - 1).append(" stops):\n");
        for (GraphNodeAL<?> node : path.pathList) {
            sb.append("→ ").append(node.getName()).append("\n");
        }
        sb.append("\nTotal Distance: ").append(path.pathCost).append(" km");
        routeOutput.setText(sb.toString());

        if (mapController != null) {
            mapController.drawRoute(path.pathList);
        }
    }
}



    @FXML
    public void onFindAllRoutes() {
    String start = startStationComboBox.getValue().trim();
    String end = destinationStationComboBox.getValue().trim();
    List<String> waypointNames = waypointsListView.getSelectionModel().getSelectedItems();

    if (start == null || end == null) {
        routeOutput.setText("Please select both stations.");
        return;
    }

    GraphNodeAL<?> startNode = graph.findStation(start);
    List<GraphAlgorithms.CostedPath> allPaths = GraphAlgorithms.findAllDFSPaths(startNode, null, 0, end);

    if (allPaths.isEmpty()) {
        routeOutput.setText("No routes found.");
        return;
    }

    //  Filter by waypoints
    if (!waypointNames.isEmpty()) {
        allPaths.removeIf(path -> !containsAllWaypoints(path.pathList, waypointNames));
    }

    if (allPaths.isEmpty()) {
        routeOutput.setText("No routes found that include all waypoints.");
        return;
    }

    StringBuilder sb = new StringBuilder("DFS: All route permutations\n");

    GraphAlgorithms.CostedPath shortest = allPaths.get(0);
    for (int i = 0; i < allPaths.size(); i++) {
        GraphAlgorithms.CostedPath path = allPaths.get(i);
        sb.append("Route ").append(i + 1).append(" (").append(path.pathCost).append(" km):\n");
        for (GraphNodeAL<?> node : path.pathList) {
            sb.append("→ ").append(node.getName()).append("\n");
        }
        sb.append("\n");

        if (path.pathCost < shortest.pathCost) {
            shortest = path;
        }
    }

    routeOutput.setText(sb.toString());

    if (mapController != null) {
        mapController.drawRoute(shortest.pathList);
    }
}


    @FXML
    public void onFindShortestRoute() {
    String start = startStationComboBox.getValue().trim();
    String end = destinationStationComboBox.getValue().trim();
    List<String> waypointNames = waypointsListView.getSelectionModel().getSelectedItems();
    List<String> avoidStations = avoidStationsListView.getSelectionModel().getSelectedItems();

    if (start == null || end == null) {
        routeOutput.setText("Please select both stations.");
        return;
    }

    GraphNodeAL<?> startNode = graph.findStation(start);
    GraphNodeAL<?> endNode = graph.findStation(end);

    GraphAlgorithms.CostedPath path;
    if (waypointNames.isEmpty()) {
        path = GraphAlgorithms.findCheapestPathDijkstra(startNode, end, avoidStations);
    } else {
        path = buildDijkstraPathWithWaypointsAvoid(startNode, endNode, waypointNames, avoidStations, graph);
    }

    if (path == null) {
        routeOutput.setText("No shortest route found (possibly due to avoided stations).");
        return;
    }

    StringBuilder sb = new StringBuilder("Dijkstra: Shortest Route\n");
    for (GraphNodeAL<?> node : path.pathList) {
        sb.append("→ ").append(node.getName()).append("\n");
    }
    sb.append("\nTotal Distance: ").append(path.pathCost).append(" km");
    routeOutput.setText(sb.toString());

    if (mapController != null) {
        mapController.drawRoute(path.pathList);
    }
}



@FXML
public void onFindRouteWithPenalty() {
    String start = startStationComboBox.getValue().trim();
    String end = destinationStationComboBox.getValue().trim();
    List<String> waypointNames = waypointsListView.getSelectionModel().getSelectedItems();
    List<String> avoidStations = avoidStationsListView.getSelectionModel().getSelectedItems();
    int penalty = (int) penaltySlider.getValue(); // Get user-selected penalty

    if (start == null || end == null) {
        routeOutput.setText("Please select both stations.");
        return;
    }

    GraphNodeAL<?> startNode = graph.findStation(start);
    GraphNodeAL<?> endNode = graph.findStation(end);

    GraphAlgorithms.CostedPath path;
    if (waypointNames.isEmpty()) {
        path = GraphAlgorithms.findCheapestPathWithPenalty(startNode, end, penalty, avoidStations);
    } else {
        path = buildDijkstraWithPenaltyPath(startNode, endNode, waypointNames, avoidStations, graph, penalty);
    }

    if (path == null) {
        routeOutput.setText("No route found (possibly due to avoided stations).");
        return;
    }

    StringBuilder sb = new StringBuilder("Dijkstra + Penalty (Penalty: ").append(penalty).append(")\n");
    for (GraphNodeAL<?> node : path.pathList) {
        sb.append("→ ").append(node.getName()).append("\n");
    }
    sb.append("\nTotal Cost: ").append(path.pathCost).append(" km");
    routeOutput.setText(sb.toString());

    if (mapController != null) {
        mapController.drawRoute(path.pathList);
    }
}



    public void setMapController(MapController mapController) {
        this.mapController = mapController;
    }

    private GraphAlgorithms.CostedPath buildPathThroughWaypoints(
        GraphNodeAL<?> startNode,
        GraphNodeAL<?> endNode,
        List<String> waypointNames,
        List<String> avoidStations,
        Graph graph) {

    GraphAlgorithms.CostedPath finalPath = new GraphAlgorithms.CostedPath();
    GraphNodeAL<?> currentStart = startNode;

    for (String waypointName : waypointNames) {
        GraphNodeAL<?> waypoint = graph.findStation(waypointName);
        if (waypoint == null) return null;

        GraphAlgorithms.CostedPath segment = GraphAlgorithms.findAnyRouteBFS(currentStart, (String) waypointName, avoidStations);
        if (segment == null) return null;

        if (!finalPath.pathList.isEmpty()) segment.pathList.remove(0);
        finalPath.pathList.addAll(segment.pathList);
        finalPath.pathCost += segment.pathCost;

        currentStart = waypoint;
    }

    GraphAlgorithms.CostedPath lastSegment = GraphAlgorithms.findAnyRouteBFS(currentStart, (String) endNode.getName(), avoidStations);
    if (lastSegment == null) return null;

    if (!finalPath.pathList.isEmpty()) lastSegment.pathList.remove(0);
    finalPath.pathList.addAll(lastSegment.pathList);
    finalPath.pathCost += lastSegment.pathCost;

    return finalPath;
}


private GraphAlgorithms.CostedPath buildDijkstraPathWithWaypoints(
    GraphNodeAL<?> startNode,
    GraphNodeAL<?> endNode,
    List<String> waypointNames,
    List<String> avoidStations,
    Graph graph) {

GraphAlgorithms.CostedPath finalPath = new GraphAlgorithms.CostedPath();
GraphNodeAL<?> currentStart = startNode;

for (String waypointName : waypointNames) {
    GraphNodeAL<?> waypoint = graph.findStation(waypointName);
    if (waypoint == null) return null;

    GraphAlgorithms.CostedPath segment = GraphAlgorithms.findCheapestPathDijkstra(currentStart, (String) waypointName, avoidStations);
    if (segment == null) return null;

    if (!finalPath.pathList.isEmpty()) segment.pathList.remove(0);
    finalPath.pathList.addAll(segment.pathList);
    finalPath.pathCost += segment.pathCost;

    currentStart = waypoint;
}

GraphAlgorithms.CostedPath lastSegment = GraphAlgorithms.findCheapestPathDijkstra(currentStart, (String) endNode.getName(), avoidStations);
if (lastSegment == null) return null;

if (!finalPath.pathList.isEmpty()) lastSegment.pathList.remove(0);
finalPath.pathList.addAll(lastSegment.pathList);
finalPath.pathCost += lastSegment.pathCost;

return finalPath;
}


    private boolean containsAllWaypoints(List<GraphNodeAL<?>> path, List<String> waypointNames) {
        for (String waypoint : waypointNames) {
            boolean found = path.stream().anyMatch(node -> node.getName().equalsIgnoreCase(waypoint));
            if (!found) return false;
        }
        return true;
    }
    
    private GraphAlgorithms.CostedPath buildBFSPathWithWaypoints(GraphNodeAL<?> startNode, GraphNodeAL<?> endNode, List<String> waypointNames, List<String> avoidStations, Graph graph) {
        GraphAlgorithms.CostedPath finalPath = new GraphAlgorithms.CostedPath();
        GraphNodeAL<?> currentStart = startNode;
    
        for (String waypointName : waypointNames) {
            GraphNodeAL<?> waypoint = graph.findStation(waypointName);
            if (waypoint == null) return null;
    
            GraphAlgorithms.CostedPath segment = GraphAlgorithms.findAnyRouteBFS(currentStart, waypointName, avoidStations);
            if (segment == null) return null;
    
            if (!finalPath.pathList.isEmpty()) segment.pathList.remove(0);
            finalPath.pathList.addAll(segment.pathList);
            finalPath.pathCost += segment.pathCost;
    
            currentStart = waypoint;
        }
    
        GraphAlgorithms.CostedPath lastSegment = GraphAlgorithms.findAnyRouteBFS(currentStart, endNode.getName(), avoidStations);
        if (lastSegment == null) return null;
    
        if (!finalPath.pathList.isEmpty()) lastSegment.pathList.remove(0);
        finalPath.pathList.addAll(lastSegment.pathList);
        finalPath.pathCost += lastSegment.pathCost;
    
        return finalPath;
    }

    private GraphAlgorithms.CostedPath buildDijkstraPathWithWaypointsAvoid(
        GraphNodeAL<?> startNode,
        GraphNodeAL<?> endNode,
        List<String> waypointNames,
        List<String> avoidStations,
        Graph graph) {

    GraphAlgorithms.CostedPath finalPath = new GraphAlgorithms.CostedPath();
    GraphNodeAL<?> currentStart = startNode;

    for (String waypointName : waypointNames) {
        GraphNodeAL<?> waypoint = graph.findStation(waypointName);
        if (waypoint == null) return null;

        GraphAlgorithms.CostedPath segment = GraphAlgorithms.findCheapestPathDijkstra(currentStart, (String) waypointName, avoidStations);
        if (segment == null) return null;

        if (!finalPath.pathList.isEmpty()) segment.pathList.remove(0);
        finalPath.pathList.addAll(segment.pathList);
        finalPath.pathCost += segment.pathCost;

        currentStart = waypoint;
    }

    GraphAlgorithms.CostedPath lastSegment = GraphAlgorithms.findCheapestPathDijkstra(currentStart, (String) endNode.getName(), avoidStations);
    if (lastSegment == null) return null;

    if (!finalPath.pathList.isEmpty()) lastSegment.pathList.remove(0);
    finalPath.pathList.addAll(lastSegment.pathList);
    finalPath.pathCost += lastSegment.pathCost;

    return finalPath;
}

private GraphAlgorithms.CostedPath buildDijkstraWithPenaltyPath(
        GraphNodeAL<?> startNode,
        GraphNodeAL<?> endNode,
        List<String> waypointNames,
        List<String> avoidStations,
        Graph graph,
        int penalty) {

    GraphAlgorithms.CostedPath finalPath = new GraphAlgorithms.CostedPath();
    GraphNodeAL<?> currentStart = startNode;

    for (String waypointName : waypointNames) {
        GraphNodeAL<?> waypoint = graph.findStation(waypointName);
        if (waypoint == null) return null;

        GraphAlgorithms.CostedPath segment = GraphAlgorithms.findCheapestPathWithPenalty(currentStart, (String) waypointName, penalty, avoidStations);
        if (segment == null) return null;

        if (!finalPath.pathList.isEmpty()) segment.pathList.remove(0);
        finalPath.pathList.addAll(segment.pathList);
        finalPath.pathCost += segment.pathCost;

        currentStart = waypoint;
    }

    GraphAlgorithms.CostedPath lastSegment = GraphAlgorithms.findCheapestPathWithPenalty(currentStart, (String) endNode.getName(), penalty, avoidStations);
    if (lastSegment == null) return null;

    if (!finalPath.pathList.isEmpty()) lastSegment.pathList.remove(0);
    finalPath.pathList.addAll(lastSegment.pathList);
    finalPath.pathCost += lastSegment.pathCost;

    return finalPath;
}

    
    

    @FXML
    public void onClear() {
    startStationComboBox.getSelectionModel().clearSelection();
    destinationStationComboBox.getSelectionModel().clearSelection();
    waypointsListView.getSelectionModel().clearSelection();
    avoidStationsListView.getSelectionModel().clearSelection();
    routeOutput.clear();

    if (mapController != null) {
        mapController.clearRoute(); // Make sure this method exists in MapController
    }
}


}
