package com.sunny.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisCacheManager implements CacheManager {
    @Autowired
    private RedisCache redisCache;

    public <K, V> Cache<K, V> getCache(String cacheName) throws CacheException {
        return redisCache;
    }
}
