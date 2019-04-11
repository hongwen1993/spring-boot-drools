package com.drools.drools01.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCommands;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class CacheManager {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RedisTemplate redisTemplate;

    public Object get(String id) {
        return redisTemplate.opsForValue().get(id);
    }

    public void put(String id, Object value) {
        redisTemplate.opsForValue().set(id, value);
    }

    public void remove(String id) {
        redisTemplate.opsForValue().getOperations().delete(id);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0
     * @return true成功 false 失败
     */
    public boolean put(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                logger.error("time must be over 0 !");
            }
            return true;
        } catch (Exception e) {
            logger.error("put error", e);
            return false;
        }
    }

    /**
     * 并发情况下使用的putString
     */
    public String putSyn(String key, String value, final long time) {
        String result = null;
        try {
            if (time > 0) {
                result = (String) redisTemplate.execute((RedisCallback) connection -> {
                    JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                    return commands.set(key, value, "NX", "PX", time * 1000);
                });
            } else {
                logger.error("time must be over 0 !");
            }
        } catch (Exception e) {
            logger.error("put error", e);
        }
        return result;
    }

    /**
     * 并发下MAP的数值操作
     */
    public Object putMapSyn(String key, String k, long L) {
        return redisTemplate.execute((RedisCallback) connection -> {
            JedisCommands commands = (JedisCommands) connection.getNativeConnection();
            return commands.hincrBy(key, k, L);
        });
    }

    /**
     * 获取指定map的指定field的值
     */
    public Object getMap(String key,  String k) {
        return redisTemplate.execute((RedisCallback) connection -> {
            JedisCommands commands = (JedisCommands) connection.getNativeConnection();
            return commands.hget(key, k);
        });
    }

    /**
     * 存入MAP
     * @param key   MAP的key
     * @param map   MAP的值
     */
    public void putMap(String key, Map map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 获取map
     * @param key   map的key
     * @return      MAP
     */
    public Map getMap(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public void incrementMap(String key, String k, long L) {
        redisTemplate.opsForHash().increment(key, k, L);
    }





}
