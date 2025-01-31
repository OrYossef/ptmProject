package configs;


import graph.Agent;
import graph.Message;

import java.util.function.BinaryOperator;

public class PlusAgent implements Agent {

    double x,y;
    String name, topicA,topicB,result;
    TopicManagerSingleton.TopicManager tm;
    BinaryOperator<Double> op;

    public PlusAgent(String[] subs,String[] pubs){
        tm = TopicManagerSingleton.get();
        name = "PlusAgent";
        topicA = subs[0];
        topicB = subs[1];
        result = pubs[0];
        op = (a,b)->a+b;

        tm.getTopic(topicA).subscribe(this);
        tm.getTopic(topicB).subscribe(this);
        tm.getTopic(result).addPublisher(this);
    }

    @Override
    public String getName() {
        return "PlusAgent";
    }

    @Override
    public void reset() {
        x = 0;
        y = 0;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(topicA)){
            x=msg.asDouble;
        }
        if (topic.equals(topicB)){
            y=msg.asDouble;
        }
        if(!Double.isNaN(x) && ! Double.isNaN(y))
            tm.getTopic(result).publish(new Message(""+op.apply(x, y)));
    }

    @Override
    public void close() {
    }
}
