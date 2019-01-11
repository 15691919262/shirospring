package com.sunny.cache;

import com.sunny.utils.JedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import java.util.Collection;
import java.util.Set;

@Component
@Slf4j
public class RedisCache<K,V> implements Cache<K,V> {

    @Autowired
    private JedisUtil jedisUtil;


    private final String SHIRO_CACHE_PREFIX = "shiro_cache_";

    private byte[] getKey(K k){
        if (k instanceof String){
            return (SHIRO_CACHE_PREFIX+k).getBytes();
        }
        return SerializationUtils.serialize(k);
    }

    public V get(K k) throws CacheException {
        log.info("从Redis中获取授权信息");
        byte[] key = getKey(k);
        byte[] value = jedisUtil.get(key);
        if (value!=null){
            return (V) SerializationUtils.deserialize(value);
        }
        return null;
    }

    public V put(K k, V v) throws CacheException {
        byte[] key = getKey(k);
        byte[] value = SerializationUtils.serialize(v);
        jedisUtil.set(key,value);
        jedisUtil.expire(key,600);
        return v;
    }

    public V remove(K k) throws CacheException {
        byte[] key = getKey(k);
        jedisUtil.del(key);
        return get(k);
    }

    public void clear() throws CacheException {

    }

    public int size() {
        return 0;
    }

    public Set<K> keys() {
        return null;
    }

    public Collection<V> values() {
        return null;
    }
}
