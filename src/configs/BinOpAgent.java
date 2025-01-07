package configs;
import graph.*;

import java.util.function.BinaryOperator;


public class BinOpAgent implements Agent  {

        private String agentName;
        private String firstTopic;
        private String secondTopic;
        private String resultTopic;
        private BinaryOperator<Double> operation;
        private double value1;
        private double value2;

        public BinOpAgent(String agName, String topic1, String topic2, String output, BinaryOperator<Double> expression) {
            this.agentName = agName;
            this.firstTopic = topic1;
            this.secondTopic = topic2;
            this.resultTopic = output;
            this.operation = expression;
            this.value1 = Double.NaN;
            this.value2 = Double.NaN;

            TopicManagerSingleton.TopicManager manager = TopicManagerSingleton.get();
            Topic t1 = manager.getTopic(topic1);
            Topic t2 = manager.getTopic(topic2);
            Topic out = manager.getTopic(output);

            t1.subscribe(this);
            t2.subscribe(this);
            out.addPublisher(this);
        }

        @Override
        public String getName() {
            return this.agentName;
        }

        @Override
        public void reset() {
            this.value1 = Double.NaN;
            this.value2 = Double.NaN;

        }

        @Override
        public void callback(String topic, Message msg) {
            if (topic.equals(firstTopic)) {
                value1 = msg.asDouble;
            } else if (topic.equals(secondTopic)) {
                value2 = msg.asDouble;
            }

            if (!Double.isNaN(value1) && !Double.isNaN(value2)) {
                double res = operation.apply(value1, value2);

                TopicManagerSingleton.TopicManager manager = TopicManagerSingleton.get();
                Topic output = manager.getTopic(resultTopic);
                output.publish(new Message(res));

                reset();

            }
        }

        @Override
        public void close() {
            TopicManagerSingleton.TopicManager manager = TopicManagerSingleton.get();

            Topic topicObj1 = manager.getTopic(firstTopic);
            topicObj1.unsubscribe(this);

            Topic topicObj2 = manager.getTopic(secondTopic);
            topicObj2.unsubscribe(this);

            Topic outputTopicObj = manager.getTopic(resultTopic);
            outputTopicObj.removePublisher(this);
        }
    }

    

