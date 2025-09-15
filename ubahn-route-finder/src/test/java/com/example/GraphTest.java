package com.example;

import com.example.Model.GraphNodeAL;
import com.example.Util.GraphAlgorithms;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    private GraphNodeAL<String>[] buildTestGraph() {
        GraphNodeAL<String> a = new GraphNodeAL<>("A", "A");
        GraphNodeAL<String> b = new GraphNodeAL<>("B", "B");
        GraphNodeAL<String> c = new GraphNodeAL<>("C", "C");
        GraphNodeAL<String> d = new GraphNodeAL<>("D", "D");

        // Connect stations
        a.addLink(b, 5, "U1", "#FF0000");
        b.addLink(c, 3, "U1", "#FF0000");
        c.addLink(d, 2, "U1", "#FF0000");
        a.addLink(d, 20, "U2", "#0000FF"); // longer direct path

        return new GraphNodeAL[]{a, b, c, d};
    }

    @Test
    public void testFindRoute() {
        GraphNodeAL<String>[] graph = buildTestGraph();
        GraphNodeAL<String> start = graph[0]; // A
        String end = "D";

        var path = GraphAlgorithms.findCheapestPathDijkstra(start, end, List.of());
        assertNotNull(path);
        assertEquals(10, path.pathCost); // A→B→C→D = 5+3+2
        assertEquals(4, path.pathList.size());
        assertEquals("A", path.pathList.get(0).getName());
        assertEquals("D", path.pathList.get(3).getName());
    }

    @Test
    public void testFindShortestRoute() {
        GraphNodeAL<String>[] graph = buildTestGraph();
        GraphNodeAL<String> start = graph[0]; // A
        String end = "D";

        var path = GraphAlgorithms.findCheapestPathDijkstra(start, end, List.of());
        assertNotNull(path);
        assertEquals(10, path.pathCost); // still A→B→C→D
        assertEquals("D", path.pathList.get(path.pathList.size() - 1).getName());
    }

    @Test
    public void testFindAllRoutes() {
        GraphNodeAL<String>[] graph = buildTestGraph();
        GraphNodeAL<String> start = graph[0]; // A

        List<GraphAlgorithms.CostedPath> paths = GraphAlgorithms.findAllDFSPaths(start, null, 0, "D");
        assertNotNull(paths);
        assertFalse(paths.isEmpty());
        boolean foundShortest = paths.stream().anyMatch(p -> p.pathCost == 10);
        assertTrue(foundShortest);
    }

    @Test
    public void testAvoidStation() {
        GraphNodeAL<String>[] graph = buildTestGraph();
        GraphNodeAL<String> start = graph[0]; // A

        var path = GraphAlgorithms.findAnyRouteBFS(start, "D", List.of("C")); // Avoid C
        assertNotNull(path);
        assertEquals(20, path.pathCost); // A→D direct
        assertEquals(2, path.pathList.size());
    }

    @Test
    public void testRouteWithPenalty() {
        GraphNodeAL<String> a = new GraphNodeAL<>("A", "A");
        GraphNodeAL<String> b = new GraphNodeAL<>("B", "B");
        GraphNodeAL<String> c = new GraphNodeAL<>("C", "C");

        // Different lines to test penalty
        a.addLink(b, 4, "U1", "#FF0000");
        b.addLink(c, 2, "U2", "#0000FF");

        var path = GraphAlgorithms.findCheapestPathWithPenalty(a, "C", 10, List.of());

        assertNotNull(path);
        assertEquals(4 + 2 + 10, path.pathCost); // cost + penalty
        assertEquals("C", path.pathList.get(path.pathList.size() - 1).getName());
    }
}

