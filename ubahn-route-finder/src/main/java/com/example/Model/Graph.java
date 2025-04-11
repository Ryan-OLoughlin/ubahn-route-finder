package com.example.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Graph {

    private Map<GraphNodeAL, List<GraphLinkAL>> adjacencyList = new HashMap<>();

    public void addStation(GraphNodeAL station) {
        adjacencyList.putIfAbsent(station, new ArrayList<>()); }

    public void addLink(GraphNodeAL source, GraphNodeAL destination, double distance, String lineName, String lineColor) {
        GraphLinkAL link = new GraphLinkAL(source, destination, distance, lineName, lineColor);
        adjacencyList.get(source).add(link);
        adjacencyList.get(destination).add(new GraphLinkAL(destination, source, distance, lineName, lineColor)); // Undirected graph
    }

    public List<GraphLinkAL> getLinks(GraphNodeAL station){
        return adjacencyList.get(station);
    }

    public Collection<GraphNodeAL> getStations(){
        return adjacencyList.keySet();
    }

    //Method to find a station by name assuming unique station names
    public GraphNodeAL findStation(String name) {
        return adjacencyList.keySet().stream()
                .filter(station -> station.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
