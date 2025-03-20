package com.example.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.Util.GraphAlgorithms.GraphNodeAL2;

public class GraphAlgorithms {

    public class GraphLinkAL {
        public GraphNodeAL2<?> destNode; //Could also store source node if required
        public int cost; //Other link attributes could be similarly stored
        public GraphLinkAL(GraphNodeAL2<?> destNode,int cost) {
        this.destNode=destNode;
        this.cost=cost;
        }
    }

    public class GraphNodeAL2<T> {
        public T data;
        public int nodeValue=Integer.MAX_VALUE;
        public List<GraphLinkAL> adjList=new ArrayList<>(); //Could use any concrete List implementation
        public GraphNodeAL2(T data) {
        this.data=data;
        }
        public void connectToNodeDirected(GraphNodeAL2<T> destNode,int cost) {
        adjList.add(new GraphLinkAL(destNode,cost));
        }
        public void connectToNodeUndirected(GraphNodeAL2<T> destNode,int cost) {
        adjList.add(new GraphLinkAL(destNode,cost));
        destNode.adjList.add(new GraphLinkAL(this,cost));
        }
    }

    //New class to hold a CostedPath object i.e. a list of GraphNodeAL2 objects and a total cost attribute
    public static class CostedPath{
        public int pathCost=0;
        public List<GraphNodeAL2<?>> pathList=new ArrayList<>();
        }
        //Retrieve cheapest path by expanding all paths recursively depth-first
        public static <T> CostedPath searchGraphDepthFirstCheapestPath(GraphNodeAL2<?> from, List<GraphNodeAL2<?>> encountered,
        int totalCost, T lookingfor){
        if(from.data.equals(lookingfor)){ //Found it - end of path
        CostedPath cp=new CostedPath(); //Create a new CostedPath object
        cp.pathList.add(from); //Add the current node to it - only (end of path) element
        cp.pathCost=totalCost; //Use the current total cost
        return cp; //Return the CostedPath object
        }
        if(encountered==null) encountered=new ArrayList<>(); //First node so create new (empty) encountered list
        encountered.add(from);
        List<CostedPath> allPaths=new ArrayList<>(); //Collection for all candidate costed paths from this node
        for(GraphLinkAL adjLink : from.adjList) //For every adjacent node
        if(!encountered.contains(adjLink.destNode)) //That has not yet been encountered
        {
        //Create a new CostedPath from this node to the searched for item (if a valid path exists)
        CostedPath temp=searchGraphDepthFirstCheapestPath(adjLink.destNode,new ArrayList<>(encountered),
        totalCost+adjLink.cost,lookingfor);
        if(temp==null) continue; //No path was found, so continue to the next iteration
        temp.pathList.add(0,from); //Add the current node to the front of the path list
        allPaths.add(temp); //Add the new candidate path to the list of all costed paths
        }
        //If no paths were found then return null. Otherwise, return the cheapest path (i.e. the one with min pathCost)
        return allPaths.isEmpty() ? null : Collections.min(allPaths, (p1,p2)->p1.pathCost-p2.pathCost);
        }

    public static <T> CostedPath findCheapestPathDijkstra(GraphNodeAL2<?> startNode, T lookingfor){
        CostedPath cp=new CostedPath(); //Create result object for cheapest path
        List<GraphNodeAL2<?>> encountered=new ArrayList<>(), unencountered=new ArrayList<>(); //Create encountered/unencountered lists
        startNode.nodeValue=0; //Set the starting node value to zero
        unencountered.add(startNode); //Add the start node as the only value in the unencountered list to start
        GraphNodeAL2<?> currentNode;
        do{ //Loop until unencountered list is empty
        currentNode=unencountered.remove(0); //Get the first unencountered node (sorted list, so will have lowest value)
        encountered.add(currentNode); //Record current node in encountered list
        if(currentNode.data.equals(lookingfor)){ //Found goal - assemble path list back to start and return it
        cp.pathList.add(currentNode); //Add the current (goal) node to the result list (only element)
        cp.pathCost=currentNode.nodeValue; //The total cheapest path cost is the node value of the current/goal node
        while(currentNode!=startNode) { //While we're not back to the start node...
        boolean foundPrevPathNode=false; //Use a flag to identify when the previous path node is identified
        for(GraphNodeAL2<?> n : encountered) { //For each node in the encountered list...
        for(GraphLinkAL e : n.adjList) //For each edge from that node...
        if(e.destNode==currentNode && currentNode.nodeValue-e.cost==n.nodeValue){ //If that edge links to the
        //current node and the difference in node values is the cost of the edge -> found path node!
        cp.pathList.add(0,n); //Add the identified path node to the front of the result list
        currentNode=n; //Move the currentNode reference back to the identified path node
        foundPrevPathNode=true; //Set the flag to break the outer loop
        break; //We've found the correct previous path node and moved the currentNode reference
        //back to it so break the inner loop
        }
        if(foundPrevPathNode) break; //We've identified the previous path node, so break the inner loop to continue
        }
        }
        //Reset the node values for all nodes to (effectively) infinity so we can search again (leave no footprint!)
        for(GraphNodeAL2<?> n : encountered) n.nodeValue=Integer.MAX_VALUE;
        for(GraphNodeAL2<?> n : unencountered) n.nodeValue=Integer.MAX_VALUE;
        return cp; //The costed (cheapest) path has been assembled, so return it!
        }
        //We're not at the goal node yet, so...
        for(GraphLinkAL e : currentNode.adjList) //For each edge/link from the current node...
        if(!encountered.contains(e.destNode)) { //If the node it leads to has not yet been encountered (i.e. processed)
        e.destNode.nodeValue=Integer.min(e.destNode.nodeValue, currentNode.nodeValue+e.cost); //Update the node value at the end
        //of the edge to the minimum of its current value or the total of the current node's value plus the cost of the edge
        if(!unencountered.contains(e.destNode)) unencountered.add(e.destNode);
        }
        Collections.sort(unencountered,(n1,n2)->n1.nodeValue-n2.nodeValue); //Sort in ascending node value order
        }while(!unencountered.isEmpty());
        return null; //No path found, so return null
    }
}
