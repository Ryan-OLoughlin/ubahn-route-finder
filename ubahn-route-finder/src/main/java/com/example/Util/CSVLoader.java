package com.example.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.example.Model.Graph;
import com.example.Model.GraphNodeAL;

public class CSVLoader {

    public static Graph loadGraph(String csvPath) {
        Graph graph = new Graph();
        List<String> lines = readLinesFromCSV(csvPath);

        // Assume the CSV format is: stationName, connectedStationName, lineName
        for (String line : lines) {
            String[] parts = line.split(",");  // Split by comma
            if (parts.length < 3) continue;  // Skip invalid lines

            String stationName = parts[0].trim();
            String connectedStationName = parts[1].trim();
            String lineName = parts[2].trim();

            GraphNodeAL <String> station = new GraphNodeAL<>(stationName,stationName);
            GraphNodeAL <String> connectedStation = new GraphNodeAL<>(connectedStationName, connectedStationName);

            graph.addStation(station);
            graph.addStation(connectedStation);  // Ensure both stations are added
            graph.addLink(station, connectedStation, 1.0, lineName);
        }
        return graph;
    }

    private static List<String> readLinesFromCSV(String csvPath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}