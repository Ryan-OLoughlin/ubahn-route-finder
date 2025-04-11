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
        System.out.println("🔄 Loading graph...");
        Graph graph = loadStations(stationsPath);
        System.out.println("✅ Loaded " + graph.getNodeList().size() + " stations.");
        loadLinks(graph, linksPath);
        return graph;
    }

    public static List<String> loadStationNames() {
        List<String> names = new ArrayList<>();
        List<String> lines = readLinesFromCSV(CSVLoader.class.getResource("/com/example/station_locations.csv").getPath());
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 1) {
                String name = parts[0].trim();
                names.add(name);
                System.out.println("📌 Station (comboBox): " + name);
            }
        }
        return names;
    }

    private static Graph loadStations(String csvPath) {
        Graph graph = new Graph();
        List<String> lines = readLinesFromCSV(csvPath);

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 3) {
                System.out.println("⚠️ Invalid station line (ignored): " + line);
                continue;
            }

            String name = parts[0].trim();
            double lat = Double.parseDouble(parts[1].trim());
            double lon = Double.parseDouble(parts[2].trim());

            System.out.println("📍 Adding station: " + name + " (" + lat + ", " + lon + ")");
            GraphNodeAL<String> station = new GraphNodeAL<>(name, name);
            station.setCoordinates(lat, lon);
            graph.addStation(station);
        }

        return graph;
    }

    private static void loadLinks(Graph graph, String csvPath) {
        List<String> lines = readLinesFromCSV(csvPath);
    
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 4) continue;
    
            String stationName = parts[0].trim();
            String connectedStationName = parts[1].trim();
            String lineName = parts[2].trim();
            String lineColor = parts[3].trim();
    
            double distance = 1.0; // Default distance if not provided
            if (parts.length >= 5) {
                try {
                    distance = Double.parseDouble(parts[4].trim());
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ Invalid distance, using default 1.0 → " + line);
                }
            } else {
                System.out.println("ℹ️ No distance provided, using default 1.0 → " + line);
            }
    
            GraphNodeAL<?> station = graph.findStation(stationName);
            GraphNodeAL<?> connectedStation = graph.findStation(connectedStationName);
    
            if (station != null && connectedStation != null) {
                System.out.println("🔗 Linking: " + stationName + " ↔ " + connectedStationName +
                                   " [" + distance + " km, Line: " + lineName + "]");
                station.addLink(connectedStation, distance, lineName, lineColor);
            } else {
                System.out.println("❌ Link failed: Cannot find → " + stationName + " or " + connectedStationName);
            }
        }
    }

    private static List<String> readLinesFromCSV(String csvPath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to read CSV file: " + csvPath);
            e.printStackTrace();
        }
        return lines;
    }
}

