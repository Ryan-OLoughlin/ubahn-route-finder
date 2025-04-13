package com.example.Controller;

import java.util.List;

import com.example.Model.Graph;
import com.example.Model.GraphNodeAL;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class MapController {

    private Canvas mapCanvas;
    private GraphicsContext gc;
    private Graph graph;

    private Image backgroundImage;

    
    public void setCanvas(Canvas canvas) {
        this.mapCanvas = canvas;
        gc = mapCanvas.getGraphicsContext2D();

        try {
            // Verify image resource exists
            String imagePath = "/com/example/images/ubahn-complete.jpg";
            if (getClass().getResource(imagePath) == null) {
                System.err.println("⛔ Image resource not found at: " + imagePath);
            }
            
            backgroundImage = new Image(getClass().getResourceAsStream(imagePath));
            
            // Immediate check for already loaded image
            if (backgroundImage.getProgress() == 1.0) {
                System.out.println("Image already loaded");
                gc.drawImage(backgroundImage, 0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
            }
            
            // Add listener for async loading
            backgroundImage.progressProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() == 1.0) {
                    System.out.println("Image loaded async - Canvas size: " 
                        + mapCanvas.getWidth() + "x" + mapCanvas.getHeight());
                    gc.drawImage(backgroundImage, 0, 0, 
                        mapCanvas.getWidth() > 0 ? mapCanvas.getWidth() : 1280,
                        mapCanvas.getHeight() > 0 ? mapCanvas.getHeight() : 720);
                }
            });

            // Verify image status
            System.out.println("Image error: " + backgroundImage.isError());
            System.out.println("Image dimensions: " + backgroundImage.getWidth() + "x" + backgroundImage.getHeight());
            System.out.println("Canvas dimensions: " + mapCanvas.getWidth() + "x" + mapCanvas.getHeight());
        }
        catch (Exception e) {
            System.err.println("Error loading map image: " + e.getMessage());
            System.err.println("⚠️ Could not load background map image.");
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
        clearCanvas();
    
        for (int i = 0; i < path.size() - 1; i++) {
            GraphNodeAL<?> from = path.get(i);
            GraphNodeAL<?> to = path.get(i + 1);
    
            // Default color if line color not found
            Color color = Color.GRAY;
    
            // Look for the actual link (edge) between the stations
            for (var link : from.getAdjList()) {
                if (link.getDestNode() == to) {
                    try {
                        color = Color.web(link.lineColor); // uses hex code or fx color name
                    } catch (IllegalArgumentException e) {
                        System.out.println("⚠️ Invalid color for line: " + link.lineColor);
                    }
                    break;
                }
            }
    
            double x1 = from.x;
            double y1 = from.y;
            double x2 = to.x;
            double y2 = to.y;
    
            gc.setStroke(color);
            gc.setLineWidth(4);
            gc.strokeLine(x1, y1, x2, y2);
        }
    
        // Draw stations
        for (GraphNodeAL<?> station : path) {
            double x = station.x;
            double y = station.y;
    
            gc.setFill(Color.DARKBLUE);
            gc.fillOval(x - 5, y - 5, 10, 10);
    
            gc.setFill(Color.BLACK);
            gc.fillText(station.name, x + 5, y - 5);
        }
    }
    
}
