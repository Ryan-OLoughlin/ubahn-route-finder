package com.example.Model;

import java.util.ArrayList;
import java.util.List;

public class GraphNodeAL<T> {
    public T data; // Generalized data storage
    public String name; // Station name
    public int nodeValue; // algorithm use
    public List<GraphLinkAL> adjList = new ArrayList<>(); // List for connected nodes
    private double latitude;
    private double longitude;
    public double x, y;

        public GraphNodeAL(T data,String name){
                this.data = data;
                this.name = name;
                this.nodeValue = Integer.MAX_VALUE;
                this.adjList = new ArrayList<>();
        }

        public void addLink(GraphNodeAL<?> connectedStation, double cost, String lineName, String lineColor){
                adjList.add(new GraphLinkAL(this, connectedStation, cost, lineName, lineColor));
                connectedStation.adjList.add(new GraphLinkAL(connectedStation, this, cost, lineName, lineColor)); // undirected graph
        }

        public double getLatitude() { 
                return latitude;
        }

        public double getLongitude() { 
                return longitude;
        }
        
        public void setCoordinates(double lat, double lon) {
                this.latitude = lat;
                this.longitude = lon;
            
                // These are raw image-space coordinates
                this.x = lon;
                this.y = lat;
            
                // Scale to canvas size
                double imageWidth = 1416.0;
                double imageHeight = 889.0;
            
                double canvasWidth = 1280.0;
                double canvasHeight = 720.0;
            
                this.x = (lon / imageWidth) * canvasWidth;
                this.y = (lat / imageHeight) * canvasHeight;
            }
            
            

        public T getData(){
                return data;
        }

        public String getName(){
                return name;
        }

        public List<GraphLinkAL> getAdjList() {
                return adjList;
        }

        public int getNodeValue() {
                return nodeValue;
        }

        public void setNodeValue(int nodeValue){
                this.nodeValue = nodeValue;
        }

        
}
