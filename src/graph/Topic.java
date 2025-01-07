package graph;


import java.util.ArrayList;
import java.util.List;

public class Topic {
    public final String name;
    public List<Agent> subs;
    public List<Agent> pubs;

    Topic(String name){
        this.name=name;
        this.subs=new ArrayList<>();
        this.pubs=new ArrayList<>();
    }

    public void subscribe(Agent a){
        subs.add(a);
    }

    public void unsubscribe(Agent a){
        int index= findIndex(subs, a);
        if (index!=-1)
            subs.remove(index);
    }

    public void publish(Message m){
        for (Agent agent: subs){
            agent.callback(name,m);
        }
    }

    public void addPublisher(Agent a){
        int index= findIndex(pubs,a);
        if (index==-1)
            pubs.add(a);
    }

    public void removePublisher(Agent a){
        int index=findIndex(pubs,a);
        if (index!=-1)
            pubs.remove(index);
    }

    public <T> int findIndex(List<T> list, T element) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(element)) {
                return i;
            }
        }
        return -1;
    }
}

