package com.example.Controller;

import java.util.List;
import com.example.Model.Graph;
import com.example.Model.GraphNodeAL;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class MapController {

    @FXML
    private Canvas mapCanvas;

    private GraphicsContext gc;
    private Graph graph;
    private Image backgroundImage;

    @FXML
    public void initialize() {
        if (mapCanvas != null) {
            gc = mapCanvas.getGraphicsContext2D();
            loadBackgroundImage();
        } else {
            System.err.println("❌ mapCanvas is not injected!");
        }
    }

    private void loadBackgroundImage() {
        try {
            String imagePath = "/com/example/images/ubahn-complete.jpg";
            if (getClass().getResource(imagePath) == null) {
                System.err.println("⛔ Image resource not found at: " + imagePath);
                return;
            }

            backgroundImage = new Image(getClass().getResourceAsStream(imagePath));

            backgroundImage.progressProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() == 1.0 && gc != null) {
                    gc.drawImage(backgroundImage, 0, 0,
                            mapCanvas.getWidth(), mapCanvas.getHeight());
                }
            });

            // Optional: Draw immediately if already loaded
            if (backgroundImage.getProgress() == 1.0 && gc != null) {
                gc.drawImage(backgroundImage, 0, 0, 1416, 889,
                        mapCanvas.getWidth(), mapCanvas.getHeight(), 0, 0);
            }

        } catch (Exception e) {
            System.err.println("⚠️ Could not load background map image: " + e.getMessage());
        }
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void clearCanvas() {
        if (gc != null && mapCanvas != null) {
            gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
            if (backgroundImage != null) {
                gc.drawImage(backgroundImage, 0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
            }
        }
    }

    public void drawRoute(List<GraphNodeAL<?>> path) {
        if (gc == null || backgroundImage == null) {
            System.err.println("❌ drawRoute called before initialization.");
            return;
        }
    
        clearCanvas();
    
        if (path == null || path.size() < 2) {
            System.out.println("⚠️ Not enough stations to draw a route.");
            return;
        }
    
        System.out.println("\n🛣️ Drawing route with " + path.size() + " stations");
        System.out.println("📏 Canvas size: " + mapCanvas.getWidth() + " x " + mapCanvas.getHeight());
        System.out.println("🖼️ Image size: " + backgroundImage.getWidth() + " x " + backgroundImage.getHeight());
    
        for (int i = 0; i < path.size() - 1; i++) {
            GraphNodeAL<?> from = path.get(i);
            GraphNodeAL<?> to = path.get(i + 1);
    
            double x1 = from.x, y1 = from.y;
            double x2 = to.x, y2 = to.y;
    
            System.out.println("➡️ Link: " + from.name + " (" + x1 + ", " + y1 + ") → " + to.name + " (" + x2 + ", " + y2 + ")");
            System.out.println("   ✅ In bounds? " + inBounds(x1, y1) + " → " + inBounds(x2, y2));
            System.out.printf("   📐 Distance = %.2f px\n", Math.hypot(x2 - x1, y2 - y1));
    
            Color color = Color.GRAY;
    
            for (var link : from.getAdjList()) {
                if (link.getDestNode() == to || link.getSrcNode() == to) {
                    try {
                        color = Color.web(link.lineColor);
                    } catch (IllegalArgumentException e) {
                        System.out.println("⚠️ Invalid color: " + link.lineColor);
                    }
                    break;
                }
            }
    
            gc.setStroke(color);
            gc.setLineWidth(4);
            gc.strokeLine(x1, y1, x2, y2);
        }
    
        for (GraphNodeAL<?> station : path) {
            double x = station.x;
            double y = station.y;
    
            System.out.println("📍 Station: " + station.name + " at (" + x + ", " + y + ") " +
                               (inBounds(x, y) ? "🟢" : "🔴 OUT OF BOUNDS"));
    
            gc.setFill(Color.DARKBLUE);
            gc.fillOval(x - 5, y - 5, 10, 10);
    
            gc.setFill(Color.BLACK);
            gc.fillText(station.name, x + 10, y - 10);
        }
    }
    
    // 🔧 Helper method
    private boolean inBounds(double x, double y) {
        return x >= 0 && x <= mapCanvas.getWidth() && y >= 0 && y <= mapCanvas.getHeight();
    }
    
    
    
    
    
    
}
