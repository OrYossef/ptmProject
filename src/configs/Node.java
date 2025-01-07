package configs;
import  graph.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;


public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    public Node(String name){
        this.name=name;
        this.edges=new ArrayList<>();
    }

    public String getName(){
        return this.name;
    }

    public List<Node> getEdges(){
        return  this.edges;
    }

    public Message getMessage(){
        return  this.msg;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setEdges(List<Node> edges){
        this.edges=edges;
    }

    public void addEdge(Node node){
            edges.add(node);

    }

    public boolean hasCycles() {
        Set<Node> visited = new HashSet<>();
        return hasCycleDFS(this, null, visited);
    }

    private boolean hasCycleDFS(Node current, Node parent, Set<Node> visited) {
        visited.add(current);

        for (Node neighbor : current.edges) {
            if (visited.contains(neighbor) && !neighbor.equals(parent)) {
                return true;
            }
            if (!visited.contains(neighbor)) {
                if (hasCycleDFS(neighbor, current, visited)) {
                    return true;
                }
            }
        }

        return false;
    }


}