package com.example.Model;


public class GraphLinkAL {
    public GraphNodeAL<?> srcNode;
    public GraphNodeAL<?> destNode;
    public double cost; // Represent distance or time
    public String line; // Subway line

    public GraphLinkAL(GraphNodeAL<?> src, GraphNodeAL<?> dest, double cost, String line){
        this.srcNode = src;
        this.destNode = dest;
        this.cost = cost;
        this.line = line;
    }

    public GraphNodeAL<?> getSrcNode(){
        return srcNode;
    }

    public GraphNodeAL<?> getDestNode(){
        return destNode;
    }

    public double getCost(){
        return cost;
    }

    public String getLine(){
        return line;
    }
    }
