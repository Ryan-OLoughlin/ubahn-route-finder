package com.example.Model;


public class GraphLinkAL {
    public GraphNodeAL<?> srcNode;
    public GraphNodeAL<?> destNode;
    public double cost; // Distance in kilometers
    public String lineName; // Subway line name
    public String lineColor; // Line color from CSV

    public GraphLinkAL(GraphNodeAL<?> src, GraphNodeAL<?> dest, double cost, String lineName, String lineColor){
        this.srcNode = src;
        this.destNode = dest;
        this.cost = cost;
        this.lineName = lineName;
        this.lineColor = lineColor;
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

    public String getLineName(){
        return lineName;
    }

    public String getLineColor(){
        return lineColor;
    }
    }
