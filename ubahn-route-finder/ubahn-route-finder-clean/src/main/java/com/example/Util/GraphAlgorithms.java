package com.example.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.example.Model.GraphLinkAL;
import com.example.Model.GraphNodeAL;

public class GraphAlgorithms {

    public static class CostedPath {
        public int pathCost = 0;
        public List<GraphNodeAL<?>> pathList = new ArrayList<>();
    }

    public static <T> List<CostedPath> findAllDFSPaths(GraphNodeAL<?> from, List<GraphNodeAL<?>> encountered,
            int totalCost, T lookingfor) {
        List<CostedPath> result = new ArrayList<>();
        
        if (from.data.equals(lookingfor)) {
            CostedPath cp = new CostedPath();
            cp.pathList.add(from);
            cp.pathCost = totalCost;
            result.add(cp);
            return result;
        }
        
        if (encountered == null) encountered = new ArrayList<>();
        encountered.add(from);
        
        for (GraphLinkAL adjLink : from.adjList) {
            if (!encountered.contains(adjLink.destNode)) {
                List<GraphNodeAL<?>> newEncountered = new ArrayList<>(encountered);
                List<CostedPath> subPaths = findAllDFSPaths(adjLink.destNode, newEncountered,
                        totalCost + (int) adjLink.cost, lookingfor);
                
                for (CostedPath path : subPaths) {
                    path.pathList.add(0, from);
                    result.add(path);
                }
            }
        }
        return result;
    }

    public static <T> CostedPath findCheapestPathDijkstra(GraphNodeAL<?> startNode, T lookingfor, List<String> avoidStations) {
        CostedPath cp = new CostedPath();
        List<GraphNodeAL<?>> encountered = new ArrayList<>(), unencountered = new ArrayList<>();
        startNode.nodeValue = 0;
        unencountered.add(startNode);
        GraphNodeAL<?> currentNode;
    
        do {
            currentNode = unencountered.remove(0);
    
            //  Skip if this is an avoided station (but not the target!)
            if (avoidStations.contains(currentNode.getName()) && !currentNode.data.equals(lookingfor)) {
                continue;
            }
    
            encountered.add(currentNode);
    
            if (currentNode.data.equals(lookingfor)) {
                cp.pathList.add(currentNode);
                cp.pathCost = currentNode.nodeValue;
    
                while (currentNode != startNode) {
                    boolean foundPrevPathNode = false;
                    for (GraphNodeAL<?> n : encountered) {
                        for (GraphLinkAL e : n.adjList) {
                            if (e.destNode == currentNode && currentNode.nodeValue - e.cost == n.nodeValue) {
                                cp.pathList.add(0, n);
                                currentNode = n;
                                foundPrevPathNode = true;
                                break;
                            }
                        }
                        if (foundPrevPathNode) break;
                    }
                }
    
                // Reset node values
                for (GraphNodeAL<?> n : encountered) n.nodeValue = Integer.MAX_VALUE;
                for (GraphNodeAL<?> n : unencountered) n.nodeValue = Integer.MAX_VALUE;
    
                return cp;
            }
    
            for (GraphLinkAL e : currentNode.adjList) {
                GraphNodeAL<?> neighbor = e.destNode;
    
                //  Skip avoided neighbors
                if (avoidStations.contains(neighbor.getName()) && !neighbor.data.equals(lookingfor)) continue;
    
                int newCost = (int) (currentNode.nodeValue + e.cost);
                if (!encountered.contains(neighbor)) {
                    if (newCost < neighbor.nodeValue) {
                        neighbor.nodeValue = newCost;
                        if (!unencountered.contains(neighbor)) unencountered.add(neighbor);
                    }
                }
            }
    
            unencountered.sort(Comparator.comparingInt(n -> n.nodeValue));
    
        } while (!unencountered.isEmpty());
    
        // Reset in case of failure
        for (GraphNodeAL<?> n : encountered) n.nodeValue = Integer.MAX_VALUE;
        for (GraphNodeAL<?> n : unencountered) n.nodeValue = Integer.MAX_VALUE;
    
        return null;
    }
    

    public static <T> CostedPath findAnyRouteBFS(GraphNodeAL<?> start, T lookingfor, List<String> avoidStations) {
        Queue<GraphNodeAL<?>> queue = new LinkedList<>();
        Map<GraphNodeAL<?>, GraphNodeAL<?>> parentMap = new HashMap<>();
        queue.add(start);
        parentMap.put(start, null);
        GraphNodeAL<?> target = null;
    
        while (!queue.isEmpty()) {
            GraphNodeAL<?> current = queue.poll();
    
            //  Skip avoided stations
            if (avoidStations.contains(current.getName())) continue;
    
            if (current.data.equals(lookingfor)) {
                target = current;
                break;
            }
    
            for (GraphLinkAL link : current.adjList) {
                GraphNodeAL<?> neighbor = link.destNode;
                if (!parentMap.containsKey(neighbor) && !avoidStations.contains(neighbor.getName())) {
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
    
        if (target == null) return null;
    
        CostedPath cp = new CostedPath();
        GraphNodeAL<?> node = target;
        while (node != null) {
            cp.pathList.add(0, node);
            node = parentMap.get(node);
        }
    
        // Calculate total cost
        for (int i = 0; i < cp.pathList.size() - 1; i++) {
            GraphNodeAL<?> a = cp.pathList.get(i);
            GraphNodeAL<?> b = cp.pathList.get(i + 1);
            for (GraphLinkAL link : a.adjList) {
                if (link.destNode == b) {
                    cp.pathCost += link.cost;
                    break;
                }
            }
        }
    
        return cp;
    }
    

    public static <T> CostedPath findCheapestPathWithPenalty(GraphNodeAL<?> startNode, T lookingfor, int penalty, List<String> avoidStations) {
        Queue<GraphNodeAL<?>> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.nodeValue));
        Map<GraphNodeAL<?>, Integer> lineCosts = new HashMap<>();
        Map<GraphNodeAL<?>, String> lastLines = new HashMap<>();
        Map<GraphNodeAL<?>, GraphNodeAL<?>> parents = new HashMap<>();
    
        startNode.nodeValue = 0;
        queue.add(startNode);
        lastLines.put(startNode, null);
        lineCosts.put(startNode, 0);
    
        while (!queue.isEmpty()) {
            GraphNodeAL<?> current = queue.poll();
    
            //  Skip avoided station (except if it's the destination)
            if (avoidStations.contains(current.getName()) && !current.data.equals(lookingfor)) {
                continue;
            }
    
            if (current.data.equals(lookingfor)) {
                CostedPath cp = new CostedPath();
                cp.pathCost = current.nodeValue;
                GraphNodeAL<?> node = current;
                while (node != null) {
                    cp.pathList.add(0, node);
                    node = parents.get(node);
                }
                return cp;
            }
    
            for (GraphLinkAL link : current.adjList) {
                GraphNodeAL<?> neighbor = link.destNode;
    
                //  Skip avoided neighbor (unless it's the destination)
                if (avoidStations.contains(neighbor.getName()) && !neighbor.data.equals(lookingfor)) {
                    continue;
                }
    
                int newCost = current.nodeValue + (int) link.cost;
                String currentLine = lastLines.get(current);
    
                if (currentLine != null && !currentLine.equals(link.lineName)) {
                    newCost += penalty;
                }
    
                if (newCost < lineCosts.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    neighbor.nodeValue = newCost;
                    lineCosts.put(neighbor, newCost);
                    lastLines.put(neighbor, link.lineName);
                    parents.put(neighbor, current);
    
                    if (!queue.contains(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }
    
        return null;
    }
}    
