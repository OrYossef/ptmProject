package configs;


import java.util.ArrayList;
import java.util.HashMap;

import graph.Agent;
import graph.Topic;
import test.TopicManagerSingleton.TopicManager;

public class Graph extends ArrayList<configs.Node>{

    public boolean hasCycles() {
        for (Node node : this) {
            if (node.hasCycles()) {
                return true;
            }
        }
        return false;
    }


    private HashMap<String,Node> nodesHelper;
    private Node getNode(String name){
        if(!nodesHelper.containsKey(name)){
            nodesHelper.put(name, new Node(name));
        }
        return nodesHelper.get(name);
    }
    public void createFromTopics(){
        clear();
        nodesHelper=new HashMap<>();

        TopicManager tm=TopicManagerSingleton.get();
        for (Topic t : tm.getTopics()){
            Node tn=getNode("T"+t.name); //create if it not exists
            for(Agent a : t.getSubs()){ // plus, minus
                Node as=getNode("A"+a.getName());
                tn.addEdge(as);
            }
            for(Agent a : t.getPubs()){
                Node ap=getNode("A"+a.getName());
                ap.addEdge(tn);
            }
        }

        addAll(nodesHelper.values());
    }


}
