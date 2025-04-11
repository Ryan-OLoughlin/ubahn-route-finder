package com.example.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.Model.Graph;
import com.example.Model.GraphNodeAL;

public class CSVLoader {

    public static Graph loadGraph(String stationsPath, String linksPath) {
        Graph graph = loadStations(stationsPath);
        loadLinks(graph, linksPath);
        return graph;
    }

    public static List<String> loadStationNames() {
        List<String> names = new ArrayList<>();
        List<String> lines = readLinesFromCSV(CSVLoader.class.getResource("/com/example/station_locations.csv").getPath());
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 1) {
                names.add(parts[0].trim());
            }
        }
        return names;
    }

    private static Graph loadStations(String csvPath) {
        Graph graph = new Graph();
        List<String> lines = readLinesFromCSV(csvPath);

        // CSV format: stationName, longtitude, latitude
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 3) continue;

            String name = parts[0].trim();
            System.out.println("Processing line" + line );
            double lat = Double.parseDouble(parts[1].trim());
            double lon = Double.parseDouble(parts[2].trim());
            
            GraphNodeAL<String> station = new GraphNodeAL<>(name, name);
            station.setCoordinates(lat, lon);
            graph.addStation(station);
        }
        return graph;
    }

    private static void loadLinks(Graph graph, String csvPath) {
        List<String> lines = readLinesFromCSV(csvPath);

        // CSV format: fromStation, toStation, lineName, lineColor, distance
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 5) continue;

            String stationName = parts[0].trim();
            String connectedStationName = parts[1].trim();
            String lineName = parts[2].trim();
            String lineColor = parts[3].trim();
            double distance = Double.parseDouble(parts[4].trim());

            GraphNodeAL<?> station = graph.findStation(stationName);
            GraphNodeAL<?> connectedStation = graph.findStation(connectedStationName);

            if (station != null && connectedStation != null) {
                graph.addLink(station, connectedStation, distance, lineName, lineColor);
            }
        }
    }

    private static List<String> readLinesFromCSV(String csvPath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            boolean isFirstLine = true; // Flag
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; 
                    continue;           // Skip the header row

                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
