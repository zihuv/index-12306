package com.zihuv.cache;

import com.zihuv.cache.core.CacheLoader;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.concurrent.TimeUnit;

import jakarta.validation.constraints.NotNull;

/**
 * 分布式缓存
 */
public interface DistributedCache extends Cache {

    /**
     * 获取缓存，如查询结果为空，调用 {@link CacheLoader} 加载缓存
     */
    <T> T get(@NotBlank String key, @NotNull Class<T> clazz, CacheLoader<T> cacheLoader);

    /**
     * 获取缓存，如查询结果为空，调用 {@link CacheLoader} 加载缓存
     */
    <T> T get(@NotBlank String key, @NotNull Class<T> clazz, CacheLoader<T> cacheLoader, long timeout);

    /**
     * 获取缓存，如查询结果为空，调用 {@link CacheLoader} 加载缓存
     */
    <T> T get(@NotBlank String key, @NotNull Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit);

    /**
     * 以一种"安全"的方式获取缓存，如查询结果为空，调用 {@link CacheLoader} 加载缓存
     * 通过此方式防止程序中可能出现的：缓存击穿、缓存雪崩场景，适用于不被外部直接调用的接口
     */
    <T> T safeGet(@NotBlank String key, @NotNull Class<T> clazz, CacheLoader<T> cacheLoader);

    /**
     * 以一种"安全"的方式获取缓存，如查询结果为空，调用 {@link CacheLoader} 加载缓存
     * 通过此方式防止程序中可能出现的：缓存击穿、缓存雪崩场景，适用于不被外部直接调用的接口
     */
    <T> T safeGet(@NotBlank String key, @NotNull Class<T> clazz, CacheLoader<T> cacheLoader, long timeout);

    /**
     * 以一种"安全"的方式获取缓存，如查询结果为空，调用 {@link CacheLoader} 加载缓存
     * 通过此方式防止程序中可能出现的：缓存击穿、缓存雪崩场景，适用于不被外部直接调用的接口
     */
    <T> T safeGet(@NotBlank String key, @NotNull Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit);

    /**
     * 放入缓存，自定义超时时间
     */
    void put(@NotBlank String key, Object value, long timeout);

    /**
     * 放入缓存，自定义超时时间
     */
    void put(@NotBlank String key, Object value, long timeout, TimeUnit timeUnit);

    /**
     * 统计指定 key 的存在数量
     */
    Long countExistingKeys(@NotEmpty String... keys);
}
