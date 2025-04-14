package com.example.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GraphNodeALTest {
    private GraphNodeAL<String> nodeA;
    private GraphNodeAL<Integer> nodeB;

    @BeforeEach
    void setUp() {
        nodeA = new GraphNodeAL<>("DataA", "StationA");
        nodeB = new GraphNodeAL<>(42, "StationB");
    }

    @Test
    void testConstructorInitialization() {
        assertEquals("DataA", nodeA.getData());
        assertEquals("StationA", nodeA.getName());
        assertEquals(Integer.MAX_VALUE, nodeA.getNodeValue());
        assertTrue(nodeA.getAdjList().isEmpty());
    }

    @Test
    void testAddLink() {
        nodeA.addLink(nodeB, 2.5, "Central", "Red");
        
        // Test nodeA's link
        GraphLinkAL linkA = nodeA.getAdjList().get(0);
        assertEquals(nodeB, linkA.destination);
        assertEquals(2.5, linkA.cost);
        assertEquals("Central", linkA.lineName);
        assertEquals("Red", linkA.lineColor);
        
        // Test nodeB's reciprocal link
        GraphLinkAL linkB = nodeB.getAdjList().get(0);
        assertEquals(nodeA, linkB.destination);
        assertEquals(2.5, linkB.cost);
    }

    @Test
    void testSetCoordinates() {
        nodeA.setCoordinates(51.5074, -0.1278);
        
        assertEquals(51.5074, nodeA.getLatitude(), 0.0001);
        assertEquals(-0.1278, nodeA.getLongitude(), 0.0001);
        
        // Verify coordinate scaling calculations
        assertEquals((-0.1278 / 1416.0) * 1280.0, nodeA.x, 0.0001);
        assertEquals((51.5074 / 889.0) * 720.0, nodeA.y, 0.0001);
    }

    @Test
    void testNodeValueManagement() {
        nodeA.setNodeValue(100);
        assertEquals(100, nodeA.getNodeValue());
        
        nodeA.setNodeValue(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, nodeA.getNodeValue());
    }

    @Test
    void testMultipleLinks() {
        GraphNodeAL<String> nodeC = new GraphNodeAL<>("DataC", "StationC");
        nodeA.addLink(nodeB, 2.5, "Central", "Red");
        nodeA.addLink(nodeC, 3.0, "District", "Green");
        
        assertEquals(2, nodeA.getAdjList().size());
        assertEquals(1, nodeB.getAdjList().size());
        assertEquals(1, nodeC.getAdjList().size());
    }
}
