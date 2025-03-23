package com.example.Util;

import java.util.Collections;

import com.example.Model.Graph;
import com.example.Model.GraphNodeAL;

public class CSVLoader {

    public static Graph loadGraph(String csvPath) {
        Graph graph = new Graph();
        // Parse CSV line-by-line:
        for (String line : lines) {
            GraphNodeAL station = new GraphNodeAL();
            graph.addStation(station);
            for (GraphNodeAL connectedStation : connectedStations) {
                graph.addConnection(station, connectedStation, lineName);
            }
        }
        return graph;
    }

}
