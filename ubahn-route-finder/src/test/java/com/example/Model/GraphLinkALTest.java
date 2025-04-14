package com.example.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GraphLinkALTest {
    private GraphNodeAL<String> nodeA;
    private GraphNodeAL<String> nodeB;

    @BeforeEach
    void setUp() {
        nodeA = new GraphNodeAL<>("DataA", "StationA");
        nodeB = new GraphNodeAL<>("DataB", "StationB");
    }

    @Test
    void testConstructorAndDirectFieldAccess() {
        GraphLinkAL link = new GraphLinkAL(nodeA, nodeB, 2.5, "Central", "Red");
        
        // Test field values directly
        assertEquals(nodeA, link.srcNode);
        assertEquals(nodeB, link.destNode);
        assertEquals(2.5, link.cost);
        assertEquals("Central", link.lineName);
        assertEquals("Red", link.lineColor);
    }

    @Test
    void testGetterMethods() {
        GraphLinkAL link = new GraphLinkAL(nodeB, nodeA, 3.0, "District", "Green");
        
        // Test getter methods
        assertEquals(nodeB, link.getSrcNode());
        assertEquals(nodeA, link.getDestNode());
        assertEquals(3.0, link.getCost(), 0.001);
        assertEquals("District", link.getLineName());
        assertEquals("Green", link.getLineColor());
    }

    @Test
    void testZeroCostLink() {
        GraphLinkAL freeLink = new GraphLinkAL(nodeA, nodeB, 0.0, "Free", "White");
        assertEquals(0.0, freeLink.getCost(), 0.001);
    }

    @Test
    void testNegativeCost() {
        GraphLinkAL negativeLink = new GraphLinkAL(nodeA, nodeB, -1.5, "Test", "Black");
        assertEquals(-1.5, negativeLink.cost, 0.001);
    }

    @Test
    void testSpecialCharactersInLineInfo() {
        GraphLinkAL specialLink = new GraphLinkAL(nodeA, nodeB, 4.2, "Line#12", "#FF0000");
        assertEquals("Line#12", specialLink.getLineName());
        assertEquals("#FF0000", specialLink.lineColor);
    }
}