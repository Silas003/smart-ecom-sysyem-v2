package com.amalitech.demo.config;


import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class CacheConfig {

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    // Standard constructor for Spring injection
    public CacheConfig() {}

    public void addToCache(String key,Object item){
        cache.put(key, item);
    }

    public void evictFromCache(String key){
        cache.remove(key);
    }

    public void evictAllFromCache(){
        cache.clear();
    }

    public Object getFromCache(String key){
        return cache.get(key);
    }
}
