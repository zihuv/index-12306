package com.zihuv.idempotent.core.context;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Maps;

import java.util.Map;

public class IdempotentContext {

    private static final ThreadLocal<Map<String, String>> CONTEXT = new ThreadLocal<>();

    public static Map<String, String> get() {
        return CONTEXT.get();
    }

    public static String getKey(String key) {
        Map<String, String> context = get();
        if (CollUtil.isNotEmpty(context)) {
            return context.get(key);
        }
        return null;
    }

    public static String getString(String key) {
        Object actual = getKey(key);
        if (actual != null) {
            return actual.toString();
        }
        return null;
    }

    public static void put(String key, String val) {
        Map<String, String> context = get();
        if (CollUtil.isEmpty(context)) {
            context = Maps.newHashMap();
        }
        context.put(key, val);
        putContext(context);
    }

    public static void putContext(Map<String, String> context) {
        Map<String, String> threadContext = CONTEXT.get();
        if (CollUtil.isNotEmpty(threadContext)) {
            threadContext.putAll(context);
            return;
        }
        CONTEXT.set(context);
    }

    public static void clean() {
        CONTEXT.remove();
    }
}