package test;

import java.util.*;

public class TopicManagerSingleton {
    public static TopicManager get(){
        return TopicManager.instance;
    }


    public static class TopicManager{
        private static  final TopicManager instance=new TopicManager();
        public Map<String,Topic > topicMap;

        private TopicManager(){
            topicMap= new HashMap<>();
        }

        public Topic getTopic(String name){
            Topic topic= topicMap.get(name);

            if (topic==null){
                topic=new Topic(name);
                topicMap.put(name,topic);
            }
            return topic;
        }

        public Collection<Topic> getTopics(){
            return topicMap.values();
        }

        public void clear(){
            topicMap.clear();
        }

    }
    
}
