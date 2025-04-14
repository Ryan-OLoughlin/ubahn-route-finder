package com.example.Controller;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RouteControllerTest {
    
    private RouteController routeController;
    private MockMapController mockMapController;
    private Graph graph;

    @BeforeAll
    static void initJfx() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        routeController = new RouteController();
        mockMapController = new MockMapController();
        
        // Initialize JavaFX components
        routeController.startStationComboBox = new ComboBox<>();
        routeController.destinationStationComboBox = new ComboBox<>();
        routeController.routeOutput = new TextArea();
        
        routeController.setMapController(mockMapController);
        initializeTestGraph();
    }

    private void initializeTestGraph() {
        graph = new Graph();
        
        // Create test nodes
        GraphNodeAL<String> nodeA = new GraphNodeAL<>("A", 0, 0);
        GraphNodeAL<String> nodeB = new GraphNodeAL<>("B", 1, 1);
        GraphNodeAL<String> nodeC = new GraphNodeAL<>("C", 2, 2);
        
        // Create connections
        nodeA.connectToNodeUndirected(nodeB, 5, "Line1");
        nodeB.connectToNodeUndirected(nodeC, 3, "Line2");
        
        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
    }

    @Test
    void setGraph_ValidGraph_SetsGraphInstance() {
        routeController.setGraph(graph);
        assertSame(graph, routeController.graph);
    }

    @Test
    void onFindRoute_ValidRoute_UpdatesOutputAndDraws() {
        routeController.setGraph(graph);
        routeController.startStationComboBox.setValue("A");
        routeController.destinationStationComboBox.setValue("C");

        routeController.onFindRoute();

        String output = routeController.routeOutput.getText();
        assertTrue(output.contains("Route found"));
        assertTrue(output.contains("A → B → C"));
        assertTrue(mockMapController.drawRouteCalled);
    }

    @Test
    void onFindRoute_NoGraph_ShowsErrorMessage() {
        routeController.setGraph(null);
        routeController.onFindRoute();
        assertEquals("Please select both start and destination stations", 
                     routeController.routeOutput.getText());
    }

    @Test
    void onFindShortestRoute_ValidRoute_UpdatesOutput() {
        routeController.setGraph(graph);
        routeController.startStationComboBox.setValue("A");
        routeController.destinationStationComboBox.setValue("C");

        routeController.onFindShortestRoute();

        String output = routeController.routeOutput.getText();
        assertTrue(output.contains("Dijkstra: Shortest Route"));
        assertTrue(output.contains("Total Distance: 8 km"));
    }

    @Test
    void onFindAllRoutes_MultiplePaths_ShowsAllRoutes() {
        // Add alternative path
        GraphNodeAL<String> nodeD = new GraphNodeAL<>("D", 3, 3);
        graph.findStation("A").connectToNodeUndirected(nodeD, 7, "Line3");
        nodeD.connectToNodeUndirected(graph.findStation("C"), 4, "Line4");

        routeController.setGraph(graph);
        routeController.startStationComboBox.setValue("A");
        routeController.destinationStationComboBox.setValue("C");

        routeController.onFindAllRoutes();

        String output = routeController.routeOutput.getText();
        assertTrue(output.contains("Route 1 (8 km)"));
        assertTrue(output.contains("Route 2 (11 km)"));
    }

    @Test
    void onFindRouteWithPenalty_ValidRoute_UpdatesOutput() {
        routeController.setGraph(graph);
        routeController.startStationComboBox.setValue("A");
        routeController.destinationStationComboBox.setValue("C");

        routeController.onFindRouteWithPenalty();

        String output = routeController.routeOutput.getText();
        assertTrue(output.contains("Penalty: 10"));
        assertTrue(output.contains("Total Cost"));
    }
}

// Manual mock implementation
class MockMapController extends MapController {
    boolean drawRouteCalled = false;
    List<GraphNodeAL<?>> drawnPath;

    @Override
    public void drawRoute(List<GraphNodeAL<?>> path) {
        drawRouteCalled = true;
        drawnPath = path;
    }
}