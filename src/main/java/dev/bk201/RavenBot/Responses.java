package dev.bk201.RavenBot;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Responses {
    JedisPool pool = new JedisPool("localhost", 6379);
    public String searchResponse(String Key, Boolean redis) {
        String response = "";

        if(redis){
            try(Jedis jedis = pool.getResource()){
                response = jedis.get(Key);
            }
        }else {
            //TODO search in sqlite
        }

        return response;
    }

    public void insertResponse(String key, String value, Boolean redis) {
        if (redis) {
            try (Jedis jedis = pool.getResource()) {
                jedis.set(key, value);
            }
        } else {
            //TODO add the possibility to add data to a sqlite data
            System.out.println("sqlite must be implemented");
        }
    }
}

