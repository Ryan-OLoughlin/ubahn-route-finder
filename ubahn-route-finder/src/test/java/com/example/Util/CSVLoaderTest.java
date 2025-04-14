package com.example.Util;

import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CSVLoaderTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void loadGraph_ValidFiles_CreatesGraphWithStationsAndLinks() throws IOException {
        // Create test station CSV
        Path stationsFile = tempDir.resolve("stations.csv");
        Files.write(stationsFile, List.of(
            "name,lat,lon",
            "Station A,51.5074,0.1278",
            "Station B,48.8566,2.3522"
        ));

        // Create test links CSV
        Path linksFile = tempDir.resolve("links.csv");
        Files.write(linksFile, List.of(
            "from,to,line,color,distance",
            "Station A,Station B,Central Line,#FF0000,5.5"
        ));

        Graph graph = CSVLoader.loadGraph(stationsFile.toString(), linksFile.toString());
        
        assertAll(
            () -> assertEquals(2, graph.getNodeList().size()),
            () -> assertTrue(outContent.toString().contains("✅ Loaded 2 stations")),
            () -> assertTrue(outContent.toString().contains("🔗 Linking: Station A ↔ Station B"))
        );
    }

    @Test
    void loadStationNames_ValidCSV_ReturnsNames() throws IOException {
        // Create test resource file
        Path testCsv = tempDir.resolve("station_locations.csv");
        Files.write(testCsv, List.of(
            "Station X,34.0522,118.2437",
            "Station Y,40.7128,74.0060"
        ));

        // Replace the resource path for testing
        CSVLoader.class.getResource("/com/example/station_locations.csv").getPath();
        // Note: In real test setup, you'd need to ensure the test CSV is in the correct resource path
        
        List<String> names = CSVLoader.loadStationNames();
        assertAll(
            () -> assertEquals(2, names.size()),
            () -> assertTrue(names.contains("Station X")),
            () -> assertTrue(outContent.toString().contains("📌 Station (comboBox): Station X"))
        );
    }

    @Test
    void loadStations_InvalidLines_SkipsInvalidEntries() throws IOException {
        Path stationsFile = tempDir.resolve("bad_stations.csv");
        Files.write(stationsFile, List.of(
            "header1,header2,header3",
            "Station A",  // Invalid line
            "Station B,invalid,coordinates"  // Invalid numbers
        ));

        Graph graph = CSVLoader.loadStations(stationsFile.toString());
        
        assertAll(
            () -> assertEquals(0, graph.getNodeList().size()),
            () -> assertTrue(outContent.toString().contains("⚠️ Invalid station line"))
        );
    }

    @Test
    void loadLinks_MissingStations_LogsErrors() throws IOException {
        Path stationsFile = tempDir.resolve("stations.csv");
        Files.write(stationsFile, List.of("name,lat,lon", "Station A,0,0"));
        
        Path linksFile = tempDir.resolve("links.csv");
        Files.write(linksFile, List.of(
            "from,to,line,color",
            "Station A,Missing Station,Line 1,#000000",
            "Missing Station,Station A,Line 1,#000000"
        ));

        Graph graph = CSVLoader.loadGraph(stationsFile.toString(), linksFile.toString());
        
        assertTrue(outContent.toString().contains("❌ Link failed: Cannot find → Missing Station"));
    }

    @Test
    void readLinesFromCSV_FileNotFound_LogsError() {
        List<String> lines = CSVLoader.readLinesFromCSV("non_existent_file.csv");
        assertTrue(lines.isEmpty());
        assertTrue(outContent.toString().contains("❌ Failed to read CSV file"));
    }

    // // Simplified Graph implementation for testing
    // static class Graph {
    //     private final List<GraphNodeAL<?>> nodes = new ArrayList<>();

    //     public void addStation(GraphNodeAL<?> station) {
    //         nodes.add(station);
    //     }

    //     public List<GraphNodeAL<?>> getNodeList() {
    //         return nodes;
    //     }

    //     public GraphNodeAL<?> findStation(String name) {
    //         return nodes.stream()
    //             .filter(n -> n.name.equals(name))
    //             .findFirst()
    //             .orElse(null);
    //     }
    // }

    // static class GraphNodeAL<T> {
    //     public String name;
    //     public double x, y;
    //     private final List<Link> adjList = new ArrayList<>();

    //     public GraphNodeAL(String name, T data) {
    //         this.name = name;
    //     }

    //     public void setCoordinates(double x, double y) {
    //         this.x = x;
    //         this.y = y;
    //     }

    //     public void addLink(GraphNodeAL<?> dest, double cost, String line, String color) {
    //         adjList.add(new Link(dest, cost, line, color));
    //     }

    //     public List<Link> getAdjList() {
    //         return adjList;
    //     }
    // }

    // static class Link {
    //     final GraphNodeAL<?> destNode;
    //     final double cost;
    //     final String line;
    //     final String lineColor;

    //     public Link(GraphNodeAL<?> destNode, double cost, String line, String lineColor) {
    //         this.destNode = destNode;
    //         this.cost = cost;
    //         this.line = line;
    //         this.lineColor = lineColor;
    //     }
    // }
}
