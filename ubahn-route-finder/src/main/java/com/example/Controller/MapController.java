package com.example.Controller;

import com.example.Model.Graph;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.example.Model.GraphNodeAL;

import java.util.List;

public class MapController {

    @FXML
    private Canvas mapCanvas;

    private GraphicsContext gc;

    private Graph graph;

    @FXML
    public void initialize() {
        gc = mapCanvas.getGraphicsContext2D();
        clearCanvas();
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void clearCanvas() {
        gc.clearRect(0,0,mapCanvas.getWidth(), mapCanvas.getHeight());
    }

    public void drawRoute(List<GraphNodeAL<?>> path) {
        clearCanvas();

        for (int i = 0; i < path.size() - 1; i++) {
            GraphNodeAL<?> from = path.get(i);
            GraphNodeAL<?> to = path.get(i + 1);

            double x1 = from.x;
            double y1 = from.y;
            double x2 = to.x;
            double y2 = to.y;

            gc.setStroke(Color.RED);
            gc.setLineWidth(4);
            gc.strokeLine(x1,y1,x2,y2);
        }

        for (GraphNodeAL<?> station : path ) {
            double x = station.x;
            double y = station.y;

            gc.setFill(Color.DARKBLUE);
            gc.fillOval(x - 5,y - 5, 10, 10);

            gc.setFill(Color.BLACK);
            gc.fillText(station.name, x + 0, y - 0);
        }
    }
}
