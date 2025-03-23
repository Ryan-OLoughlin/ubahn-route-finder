package com.example.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;


public class Graph {

    private Map<GraphNodeAL, List<GraphLinkAL>> adjacencyList = new HashMap<>();

    public void addStation(GraphNodeAL s) { 
        adjacencyList.put(s, new ArrayList<>()); }

    public void addLink(GraphNodeAL s1, GraphNodeAL s2, String line) { 
        double distance = calculateDistance(s1, s2); // Euclidean formula
        adjacencyList.get(s1).add(new GraphLinkAL(s1, s2, distance, line));
        adjacencyList.get(s2).add(new GraphLinkAL(s2, s1, distance, line)); // Undirected graph
    }
}
