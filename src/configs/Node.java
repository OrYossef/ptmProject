package configs;

import graph.Message;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    public void setMessage(Message msg){
        this.msg=msg;
    }

    public Message getMessage(){
        return msg;
    }

    public String getName() {
        return name;
    }

    public List<Node> getEdges() {
        return edges;
    }

    public void addEdge(Node node) {
        edges.add(node);
    }

    public boolean hasCycles() {
        return hasCycles(this, new ArrayList<>());
    }

    private boolean hasCycles(Node node, List<Node> visited) {
        if (visited.contains(node)) {
            return true;
        }

        visited.add(node);

        for (Node neighbor : node.getEdges()) {
            if (hasCycles(neighbor, new ArrayList<>(visited))) {
                return true; // Cycle found
            }
        }

        return false;
    }

}