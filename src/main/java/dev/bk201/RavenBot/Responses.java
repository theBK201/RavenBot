package dev.bk201.RavenBot;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Responses {
    JedisPool pool = new JedisPool("127.0.0.1", 6379);
    public String searchResponse(String Key, boolean redis) {
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
    public boolean checkForDuplicate(String key, boolean redis){
        boolean duplicate = false;
        if (redis){
            try (Jedis jedis = pool.getResource()){
                if (jedis.exists(key)){
                    duplicate = true;
                }else {
                    duplicate = false;
                }
            }
        }else {
            //TODO check in sqlite
        }
        return duplicate;
    }

    public boolean insertResponse(String key, String value, Boolean redis) {
        boolean done = false;
        if (redis) {
            try (Jedis jedis = pool.getResource()) {
                jedis.set(key, value);
                done = true;
            }
        } else {
            //TODO add the possibility to add data to a sqlite data
            System.out.println("sqlite must be implemented");
        }
        return done;
    }
}

