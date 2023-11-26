package com.zihuv.cache.core;

/**
 * 缓存加载器
 */
@FunctionalInterface
public interface CacheLoader<T> {

    /**
     * 从数据库查询数据，用于加载缓存
     */
    T load();
}
