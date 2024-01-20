package com.zihuv.base.util;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.zihuv.base.context.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class JSON {

    @Lazy
    private static final ObjectMapper objectMapper = ApplicationContextHolder.getBean(ObjectMapper.class);

    /**
     * 将 object 序列化为 json
     *
     * @param object 转换成 json 的数据
     * @return java.lang.String json 字符串
     */
    public static String toJsonStr(Object object) {
        if (object == null) {
            return null;
        }
        // 集合中不允许出现 null
        if (object instanceof List<?> list) {
            if (list.stream().anyMatch(Objects::isNull)) {
                log.error("json 序列化错误：{}", object);
                return null;
            }
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("json 序列化错误：{}", object, e);
            return null;
        }
    }

    /**
     * 将 json 反序列化为 javaBean
     *
     * @param object  Object 类型的 json
     * @param tClass 目标数据类型字节码
     * @return T 目标数据类型
     */
    public static <T> T toBean(Object object, Class<T> tClass) {
        return toBean(JSON.toJsonStr(object),tClass);
    }

    /**
     * 将 json 反序列化为 javaBean
     *
     * @param json   json 字符串
     * @param tClass 目标数据类型字节码
     * @return T 目标数据类型
     */
    public static <T> T toBean(String json, Class<T> tClass) {
        try {
            return objectMapper.readValue(json, tClass);
        } catch (JsonProcessingException e) {
            log.error("json 反序列化错误：{}", json, e);
            throw new IllegalArgumentException("json 反序列化错误", e);
        }
    }

    public static <T> T toBean(byte[] json, Class<T> tClass) {
        try {
            return objectMapper.readValue(json, tClass);
        } catch (JsonProcessingException e) {
            log.error("json 反序列化错误：{}", json, e);
            throw new IllegalArgumentException("json 反序列化错误", e);
        } catch (IOException e) {
            log.error("json 反序列化错误：{}", json, e);
            throw new RuntimeException("json 反序列化错误", e);
        }
    }

    /**
     * 将 json 反序列化为集合
     *
     * @param json   json 串
     * @param tClass 目标数据类型字节码
     * @return 目标数据类型的集合
     */
    public static <T> List<T> toList(String json, Class<T> tClass) {
        if (json == null || "null".equals(json) || "[null]".equals(json)) {
            return new ArrayList<>();
        }
        try {
            // 确保 json 能转换成 javabean，而不是 LinkedHashMap
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, tClass);
            List<T> result = objectMapper.readValue(json, listType);
            return CollUtil.isNotEmpty(result) ? result : new ArrayList<>();
        } catch (JsonProcessingException e) {
            log.error("json 反序列化为集合错误：{}", json, e);
            return new ArrayList<>();
        }
    }
}