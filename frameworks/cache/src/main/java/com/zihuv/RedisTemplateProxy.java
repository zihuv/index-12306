package com.zihuv;

import com.zihuv.config.RedisDistributedProperties;
import com.zihuv.core.CacheLoader;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Validated
@RequiredArgsConstructor
public class RedisTemplateProxy implements DistributedCache {

    private final RedisDistributedProperties redisProperties;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    private static final String SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX = "safe_get_distributed_lock_get:";

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        return clazz.isInstance(value) ? clazz.cast(value) : null;
    }

    @Override
    public void put(String key, Object value) {
        this.put(key, value, redisProperties.getValueTimeout());
    }

    @Override
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public <T> T get(String key, Class<T> clazz, CacheLoader<T> cacheLoader) {
        return get(key, clazz, cacheLoader, redisProperties.getValueTimeout());
    }

    @Override
    public <T> T get(String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout) {
        return get(key, clazz, cacheLoader, timeout, redisProperties.getValueTimeUnit());
    }

    @Override
    public <T> T get(String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit) {
        T result = get(key, clazz);
        if (result != null) {
            return result;
        }
        return loadAndSet(key, cacheLoader, timeout, timeUnit);
    }

    @Override
    public <T> T safeGet(String key, Class<T> clazz, CacheLoader<T> cacheLoader) {
        return safeGet(key, clazz, cacheLoader, redisProperties.getValueTimeout());
    }

    @Override
    public <T> T safeGet(String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout) {
        return safeGet(key, clazz, cacheLoader, timeout, redisProperties.getValueTimeUnit());
    }

    @Override
    public <T> T safeGet(String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit) {
        T result = get(key, clazz);
        // 缓存不为空，直接返回数据
        if (result != null) {
            return result;
        }
        RLock lock = redissonClient.getLock(SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX + key);
        // 获取非阻塞式锁
        if (lock.tryLock()) {
            try {
                result = cacheLoader.load();
                this.put(key, result, timeout, timeUnit);
            } finally {
                lock.unlock();
            }
        }

        // TODO 拿不到锁返回默认值
        return result;
    }

    @Override
    public void put(String key, Object value, long timeout) {
        this.put(key, value, timeout, redisProperties.getValueTimeUnit());
    }

    @Override
    public void put(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    @Override
    public Long countExistingKeys(String... keys) {
        return redisTemplate.countExistingKeys(List.of(keys));
    }

    /***
     * 加载数据并写入缓存
     *
     * @param key key
     * @param cacheLoader 缓存加载器
     * @param timeout 过期时间
     * @param timeUnit 时间单位
     * @return T 查询结果
     */
    private <T> T loadAndSet(String key, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit) {
        T result = cacheLoader.load();
        // 查询不到该数据，缓存 null 值，过期时间设置为 null 默认值
        if (result == null) {
            timeout = redisProperties.getNullValueTimeout();
        }
        this.put(key, result, timeout, timeUnit);
        return result;
    }
}