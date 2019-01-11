package com.sunny.utils;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Component
public class JedisUtil {
    @Resource
    private JedisPool jedisPool;

    private Jedis getResource(){
        return jedisPool.getResource();
    }

    /**
     * 存放
     * @param key 键
     * @param value 值
     * @return 值
     */
    public byte[] set(byte[] key, byte[] value) {
        Jedis jedis = getResource();
        try {
            jedis.set(key,value);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            jedis.close();
        }
    }

    /**
     * 给相应的key设置超时时间
     * @param key 过期的键值
     * @param second 过期时间(单位：秒)
     */
    public void expire(byte[] key, int second) {
        Jedis jedis = getResource();
        try {
            jedis.expire(key,second);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }

    /**
     * 根据键获取值
     * @param key 键
     * @return 值
     */
    public byte[] get(byte[] key) {
        Jedis jedis = getResource();
        try {
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            jedis.close();
        }
    }

    /**
     * 根据键删除值
     * @param key 键
     */
    public void del(byte[] key) {
        Jedis jedis = getResource();
        try {
            jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }

    /**
     * 模糊匹配所有prefix前缀的键集合
     * @param prefix 前缀
     * @return 所有prefix前缀的键集合
     */
    public Set<byte[]> keys(String prefix) {
        Set<byte[]> keys = new HashSet<byte[]>();
        Jedis jedis = getResource();
        try {
            keys = jedis.keys(prefix.getBytes());
            return keys;
        } catch (Exception e) {
            e.printStackTrace();
            return keys;
        } finally {
            jedis.close();
        }
    }
}
