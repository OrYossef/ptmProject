package test;

public class IncAgent implements Agent {

    double x;
    String name,topicA,result;
    TopicManagerSingleton.TopicManager tm;

    public IncAgent(String[] subs,String[] pubs){
        tm = TopicManagerSingleton.get();
        name = "PlusAgent";
        topicA = subs[0];
        result = pubs[0];

        tm.getTopic(topicA).subscribe(this);
        tm.getTopic(result).addPublisher(this);
    }


    @Override
    public String getName() {
        return "IncAgent";
    }

    @Override
    public void reset() {
        x = 0;
    }

    @Override
    public void callback(String topic, Message msg) {
        if(topic.equals(topicA)) {
            x=msg.asDouble;
        }

        if(!Double.isNaN(x)) {
            tm.getTopic(result).publish(new Message(""+x+1));
        }
    }

    @Override
    public void close() {
    }

}
