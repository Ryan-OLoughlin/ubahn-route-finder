package com.example.Model;

import java.util.ArrayList;
import java.util.List;

public class GraphNodeAL<T> {
        public T data;
        public int nodeValue=Integer.MAX_VALUE;
        public List<GraphLinkAL> adjList=new ArrayList<>(); //Could use any concrete List implementation
        public GraphNodeAL(T data) {
        this.data=data;
        }
        public void connectToNodeUndirected(GraphNodeAL<T> destNode,int cost, String line) {
        adjList.add(new GraphLinkAL(this,destNode,cost, line));
        destNode.adjList.add(new GraphLinkAL(destNode,this,cost, line));
        }
}
