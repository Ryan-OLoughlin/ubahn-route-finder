package com.example.Model;


public class GraphLinkAL {
    // makes up each connection between stations
    // each line has a src, dest, and cost
        public GraphNodeAL<?> srcNode;
        public GraphNodeAL<?> destNode; 
        public double cost;
        public String line;

        public GraphLinkAL(GraphNodeAL<?> srcNode, GraphNodeAL<?> destNode, double cost, String line){ 
            this.srcNode=srcNode;
            this.destNode=destNode;
            this.cost=cost;
            this.line=line;
        }
    }
