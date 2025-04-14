package com.example.Util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GraphAlgorithmsTest {

    private GraphNodeAL<String> nodeA;
    private GraphNodeAL<String> nodeB;
    private GraphNodeAL<String> nodeC;
    private GraphNodeAL<String> nodeD;

    @BeforeEach
    void setUp() {
        // Create test nodes
        nodeA = new GraphNodeAL<>("A", "A");
        nodeB = new GraphNodeAL<>("B", "B");
        nodeC = new GraphNodeAL<>("C", "C");
        nodeD = new GraphNodeAL<>("D", "D");

        // Create connections
        nodeA.adjList.add(new GraphLinkAL(nodeB, 5, "Line1", "Red"));
        nodeB.adjList.add(new GraphLinkAL(nodeC, 3, "Line2", "Blue"));
        nodeA.adjList.add(new GraphLinkAL(nodeD, 10, "Line3", "Green"));
        nodeD.adjList.add(new GraphLinkAL(nodeC, 2, "Line4", "Yellow"));
    }

    @Test
    void findAllDFSPaths_FindsMultiplePaths() {
        List<GraphAlgorithms.CostedPath> paths = 
            GraphAlgorithms.findAllDFSPaths(nodeA, null, 0, "C");

        assertAll(
            () -> assertEquals(2, paths.size()),
            () -> assertEquals(8, paths.get(0).pathCost),
            () -> assertEquals(12, paths.get(1).pathCost),
            () -> assertTrue(paths.stream().anyMatch(p -> 
                p.pathList.size() == 3 && 
                p.pathList.get(0).data.equals("A") &&
                p.pathList.get(2).data.equals("C")
            ))
        );
    }

    @Test
    void findCheapestPathDijkstra_ReturnsShortestPath() {
        GraphAlgorithms.CostedPath path = 
            GraphAlgorithms.findCheapestPathDijkstra(nodeA, "C");

        assertAll(
            () -> assertEquals(3, path.pathList.size()),
            () -> assertEquals("A", path.pathList.get(0).data),
            () -> assertEquals("D", path.pathList.get(1).data),
            () -> assertEquals("C", path.pathList.get(2).data),
            () -> assertEquals(12, path.pathCost)
        );
    }

    @Test
    void findAnyRouteBFS_FindsShortestPath() {
        GraphAlgorithms.CostedPath path = 
            GraphAlgorithms.findAnyRouteBFS(nodeA, "C");

        assertAll(
            () -> assertEquals(3, path.pathList.size()),
            () -> assertEquals("A", path.pathList.get(0).data),
            () -> assertEquals("B", path.pathList.get(1).data),
            () -> assertEquals("C", path.pathList.get(2).data),
            () -> assertEquals(8, path.pathCost)
        );
    }

    @Test
    void findCheapestPathWithPenalty_AccountsForLineChanges() {
        // Add alternative path with line change
        nodeB.adjList.add(new GraphLinkAL(nodeD, 4, "Line5", "Purple"));
        nodeD.adjList.add(new GraphLinkAL(nodeC, 2, "Line4", "Yellow"));

        GraphAlgorithms.CostedPath path = 
            GraphAlgorithms.findCheapestPathWithPenalty(nodeA, "C", 3);

        assertAll(
            () -> assertEquals(4, path.pathList.size()),
            () -> assertEquals(14, path.pathCost), // 5 + 4 + 2 + 3 penalty
            () -> assertTrue(path.pathList.stream()
                .map(n -> n.data)
                .toList()
                .containsAll(List.of("A", "B", "D", "C")))
        );
    }

    @Test
    void findAnyRouteBFS_ReturnsNullWhenNoPath() {
        GraphNodeAL<String> isolatedNode = new GraphNodeAL<>("Z", "Z");
        GraphAlgorithms.CostedPath path = 
            GraphAlgorithms.findAnyRouteBFS(isolatedNode, "A");
        
        assertNull(path);
    }

    // Supporting classes
    static class GraphNodeAL<T> {
        public Object data;
        public List<GraphLinkAL> adjList = new ArrayList<>();
        public int nodeValue = Integer.MAX_VALUE;

        public GraphNodeAL(String name, T data) {
            this.data = data;
        }
    }

    static class GraphLinkAL {
        public GraphNodeAL<?> destNode;
        public double cost;
        public String lineName;
        public String lineColor;

        public GraphLinkAL(GraphNodeAL<?> destNode, double cost, String lineName, String lineColor) {
            this.destNode = destNode;
            this.cost = cost;
            this.lineName = lineName;
            this.lineColor = lineColor;
        }
    }
}
