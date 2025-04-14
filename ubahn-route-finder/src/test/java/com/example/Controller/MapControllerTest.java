package com.example.Controller;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MapControllerTest {

    private MapController mapController;
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    private Canvas testCanvas;

    @BeforeAll
    static void initJfx() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        mapController = new MapController();
        testCanvas = new Canvas(800, 600);
        System.setErr(new PrintStream(errContent));
    }

    @Test
    void initialize_WithNullCanvas_PrintsErrorMessage() {
        mapController.mapCanvas = null;
        mapController.initialize();
        assertTrue(errContent.toString().contains("❌ mapCanvas is not injected!"));
    }

    @Test
    void initialize_WithValidCanvas_SetsGraphicsContext() {
        mapController.mapCanvas = testCanvas;
        mapController.initialize();
        assertNotNull(mapController.gc);
    }

    @Test
    void clearCanvas_WithBackgroundImage_RedrawsImage() {
        mapController.mapCanvas = testCanvas;
        mapController.initialize();
        mapController.backgroundImage = new Image("dummy.jpg"); // Simplified image
        
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        mapController.clearCanvas();
        
        assertTrue(outContent.toString().isEmpty()); // Verify no errors
        System.setOut(System.out);
    }

    @Test
    void drawRoute_WithoutInitialization_PrintsError() {
        mapController.drawRoute(List.of(new GraphNodeAL<>("A", 10, 10)));
        assertTrue(errContent.toString().contains("❌ drawRoute called before initialization"));
    }

    @Test
    void drawRoute_WithShortPath_PrintsWarning() {
        mapController.mapCanvas = testCanvas;
        mapController.initialize();
        mapController.backgroundImage = new Image("dummy.jpg");
        
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        mapController.drawRoute(List.of(new GraphNodeAL<>("A", 10, 10)));
        
        assertTrue(outContent.toString().contains("⚠️ Not enough stations"));
        System.setOut(System.out);
    }

    @Test
    void inBounds_WithVariousCoordinates_ReturnsCorrect() {
        mapController.mapCanvas = new Canvas(100, 100);
        
        assertAll(
            () -> assertTrue(mapController.inBounds(50, 50)),
            () -> assertTrue(mapController.inBounds(0, 0)),
            () -> assertFalse(mapController.inBounds(-1, 50)),
            () -> assertFalse(mapController.inBounds(150, 50))
        );
    }

    @Test
    void drawRoute_WithValidPath_ProducesOutput() {
        mapController.mapCanvas = testCanvas;
        mapController.initialize();
        mapController.backgroundImage = new Image("dummy.jpg");
        
        GraphNodeAL<String> node1 = new GraphNodeAL<>("A", 10, 10);
        GraphNodeAL<String> node2 = new GraphNodeAL<>("B", 20, 20);
        node1.connectToNodeUndirected(node2, 5, "#FFFFFF");
        
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        mapController.drawRoute(List.of(node1, node2));
        
        String output = outContent.toString();
        assertTrue(output.contains("🛣️ Drawing route"));
        assertTrue(output.contains("➡️ Link: A"));
        assertTrue(output.contains("📍 Station: B"));
        System.setOut(System.out);
    }

    // Helper class for testing
    static class GraphNodeAL<T> {
        public double x, y;
        public String name;
        
        public GraphNodeAL(String name, double x, double y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
        
        public void connectToNodeUndirected(GraphNodeAL<T> node, int cost, String color) {
            // Simplified connection logic for testing
        }
    }
}