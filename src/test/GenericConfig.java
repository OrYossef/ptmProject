package test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenericConfig implements Config {

    private String fileName;

    @Override
    public void create() {
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void close() {

    }

    public void setConfFile(String name) {
        List<Agent> agents = new ArrayList<>();
        Stream<String> s = null;
        try{
            s = Files.lines(Paths.get(name));
            List<String> lines = s.collect(Collectors.toList());
            if(lines.size() % 3 == 0){
                for(int i = 0; i < lines.size(); i += 3){
                    String className = lines.get(i);
                    String[] subs = lines.get(i+1).split(",");
                    String[] pubs = lines.get(i+2).split(",");

                    Class<?> c = Class.forName(className);
                    if(Agent.class.isAssignableFrom(c)){
                        Agent a = (Agent) c.getConstructor(String[].class,String[].class).newInstance(subs,pubs);
                        agents.add(a);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

