package com.example.Model;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



class GraphTest {
    private Graph graph;
    private GraphNodeAL<String> stationA;
    private GraphNodeAL<String> stationB;
    private GraphNodeAL<String> stationC;

    @BeforeEach
    public void setUp() {
        graph = new Graph();
        // Create stations with both data and name parameters
        stationA = new GraphNodeAL<>("StationAData", "Station A");
        stationB = new GraphNodeAL<>("StationBData", "Station B");
        stationC = new GraphNodeAL<>("StationCData", "Station C");
        
        // Add to graph through the Graph class methods
        graph.addStation(stationA);
        graph.addStation(stationB);
        graph.addStation(stationC);
    }

    @Test
    public void testAddStation() {
        GraphNodeAL<String> newStation = new GraphNodeAL<>("NewData", "New Station");
        graph.addStation(newStation);
        
        assertTrue(graph.getStations().contains(newStation),
        "Added station should be in stations list");
        assertEquals(0, graph.getLinks(newStation).size(),
        "New station should have no links");
    }

    @Test
    public void testAddLink() {
        graph.addLink(stationA, stationB, 2.5, "Central", "Red");
        
        // Test source link
        GraphLinkAL linkAB = graph.getLinks(stationA).get(0);
        assertEquals(stationB, linkAB.destNode,
        "Link should connect to correct destination");
        assertEquals(2.5, linkAB.cost,
        "Link should have correct distance");
        assertEquals("Central", linkAB.lineName,
        "Link should have correct line name");
        assertEquals("Red", linkAB.lineColor,
        "Link should have correct line color");
        
        // Test reverse link
        GraphLinkAL linkBA = graph.getLinks(stationB).get(0);
        assertEquals(stationA, linkBA.destNode,
        "Reverse link should connect back to source");
    }

    @Test
    void testGetLinks() {
        graph.addLink(stationA, stationB, 2.5, "Central", "Red");
        graph.addLink(stationA, stationC, 3.0, "District", "Green");
        
        assertEquals(2, graph.getLinks(stationA).size(),
        "Station A should have 2 links");
        assertEquals(1, graph.getLinks(stationB).size(),
        "Station B should have 1 link");
    }

    @Test
    void testGetStations() {
        Collection<GraphNodeAL> stations = graph.getStations();
        assertEquals(3, stations.size(),
        "Should contain exactly 3 stations");
        assertTrue(stations.containsAll(List.of(stationA, stationB, stationC)),
        "Should contain all initialized stations");
    }

    @Test
    void testAddNode() {
        GraphNodeAL<Integer> intNode = new GraphNodeAL<>(42, "Number Station");
        graph.addNode(intNode);
        
        assertTrue(graph.getNodeList().contains(intNode),
        "Node list should contain added node");
        assertFalse(graph.getStations().contains(intNode),
        "Node should not appear in stations unless explicitly added");
    }

    @Test
    void testFindStation() {
        assertEquals(stationA, graph.findStation("Station A"),
        "Should find station by exact name match");
        assertNull(graph.findStation("Unknown Station"),
        "Should return null for non-existent stations");
    }

    @Test
    void testUndirectedGraphSymmetry() {
        graph.addLink(stationA, stationB, 2.5, "Central", "Red");
        
        List<GraphLinkAL> linksAB = graph.getLinks(stationA);
        List<GraphLinkAL> linksBA = graph.getLinks(stationB);
        
        assertEquals(1, linksAB.size(),
        "Source should have 1 link");
        assertEquals(1, linksBA.size(),
        "Destination should have 1 link");
        
        GraphLinkAL forward = linksAB.get(0);
        GraphLinkAL reverse = linksBA.get(0);
        
        assertEquals(forward.cost, reverse.cost,
        "Link costs should match");
        assertEquals(forward.lineName, reverse.lineName,
        "Line names should match");
        assertEquals(forward.lineColor, reverse.lineColor,
        "Line colors should match");
    }

    @Test
    void testMultipleParallelLinks() {
        graph.addLink(stationA, stationB, 2.5, "Central", "Red");
        graph.addLink(stationA, stationB, 3.0, "Overground", "Orange");
        
        assertEquals(2, graph.getLinks(stationA).size(),
        "Should allow multiple links between stations");
        assertEquals(2, graph.getLinks(stationB).size(),
        "Reverse links should also be duplicated");
    }

    @Test
    void testNodeValueManagement() {
        stationA.setNodeValue(100);
        assertEquals(100, stationA.getNodeValue(),
        "Should update node value correctly");
    }

    @Test
    void testCoordinateConversion() {
        stationA.setCoordinates(51.5074, -0.1278);
        
        assertNotEquals(0, stationA.x,
        "X coordinate should be calculated");
        assertNotEquals(0, stationA.y,
        "Y coordinate should be calculated");
        assertEquals(51.5074, stationA.getLatitude(),
        "Latitude should be stored precisely");
        assertEquals(-0.1278, stationA.getLongitude(),
        "Longitude should be stored precisely");
    }
}