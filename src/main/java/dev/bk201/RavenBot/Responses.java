package dev.bk201.RavenBot;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

public class Responses {
    JedisPool pool = new JedisPool("127.0.0.1", 6379);

    public String searchResponse(String Key, boolean redis) {
        String response = "";

        if(redis){
            try(Jedis jedis = pool.getResource()){
                Set<String> rKeys;

                rKeys = jedis.keys("*");
                Iterator<String> iterator = rKeys.iterator();

                while (iterator.hasNext()){
                    String data = iterator.next();
                    if (Key.startsWith(data)){
                        response = jedis.get(data);
                        break;
                    }
                }

            }
        }
        return response;
    }

    public List<String> giveAllResponses(boolean redis){
        Set<String> redisKeys;
        List<String> keyList = new ArrayList<>();
        if (redis){
            try (Jedis jedis = pool.getResource()){
                redisKeys = jedis.keys("*");
                Iterator<String> it = redisKeys.iterator();

                while (it.hasNext()){
                    String data = it.next();
                    keyList.add(data);
                }
            }
        }
        return keyList;
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

    public void editResponse(String key, String newValue){
        try (Jedis jedis = pool.getResource()){
            jedis.set(key, newValue);
        }
    }

    public void deleteResponse(String key){
        try (Jedis jedis = pool.getResource()){
            jedis.del(key);
        }
    }
}

