package com.realstate.habitar.infraestructure.config.data;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GlobalVariablesCache {

    private final Map<String,String> concurrentMapVariables = new ConcurrentHashMap<>();

    public void put(String key,String value){
        concurrentMapVariables.put(key,value);
    }

    public String get(String key){
        return concurrentMapVariables.get(key);
    }

    public Map<String,String> getAll(){
        return concurrentMapVariables;
    }

}
