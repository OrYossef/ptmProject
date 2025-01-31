package configs;

import java.util.function.BinaryOperator;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton.TopicManager;

public class BinOpAgent implements Agent {

    double x,y;
    String name,topicA,topicB,result;
    TopicManager tm;
    BinaryOperator<Double> op;
    public BinOpAgent(String name,String topicA, String topicB,String result, BinaryOperator<Double> op){
        tm=TopicManagerSingleton.get();
        tm.getTopic(topicA).subscribe(this);
        tm.getTopic(topicB).subscribe(this);
        tm.getTopic(result).addPublisher(this);
        this.topicA=topicA;
        this.topicB=topicB;
        this.result=result;
        this.name=name;
        this.op=op;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        x=0;
        y=0;
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







