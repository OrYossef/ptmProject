package configs;
import graph.*;

import java.util.ArrayList;
import java.util.Collection;


public class Graph extends ArrayList<Node> {
    
    private ArrayList<Node> nodes;

    public Graph() {
        this.nodes = new ArrayList<>();
    }

    public boolean hasCycles() {
        for (Node node : nodes) {
            if (node.hasCycles())
                return true;
        }
        return false;
    }

    public void createFromTopics() {
        TopicManagerSingleton.TopicManager manager = TopicManagerSingleton.get();
        Collection<Topic> topicsList = manager.getTopics();

        for (Topic tp : topicsList) {
            Node topicNode= createOrReturnNode("T"+tp.name);

            for (Agent ag : tp.subs) {
                Node agentNode = createOrReturnNode("A" + ag.getName());
                topicNode.addEdge(agentNode);

                for (Agent agFromPubs: tp.pubs){
                    if (agFromPubs.getName().equals(ag.getName())){
                        agentNode.addEdge(topicNode);
                    }
                }

            }
        }

    }

    private Node createOrReturnNode(String name) {
        for (Node node : nodes) {
            if (node.getName().equals(name))
                return node;

        }
        Node newNode= new Node(name);
        nodes.add(newNode);
        return newNode;
    }

}

