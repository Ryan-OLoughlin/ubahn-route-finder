package com.example.Controller;

import com.example.Model.Graph;
import com.example.Model.GraphNodeAL;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public class MapController {

    @FXML
    private Canvas mapCanvas;

    private GraphicsContext gc;
    private Graph graph;

    private Image backgroundImage;

    @FXML
    public void initialize() {
        gc = mapCanvas.getGraphicsContext2D();

        try {
            backgroundImage = new Image(getClass().getResourceAsStream("/com/example/images/some-removed-grey-ubahn-map.png"));
        } catch (Exception e) {
            System.err.println("⚠️ Could not load background map image!");
        }

        clearCanvas();
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void clearCanvas() {
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
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
