package com.zihuv.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zihuv.base.context.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class JSON {

    private static final ObjectMapper objectMapper = ApplicationContextHolder.getBean(ObjectMapper.class);

    /**
     * 将 object 转换成 json
     *
     * @param object 转换成 json 的数据
     * @return java.lang.String json 字符串
     */
    public static String toJsonStr(Object object) {
        if (object == null) {
            return null;
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
     * @param json   json 字符串
     * @param tClass 目标数据类型字节码
     * @return T 目标数据类型
     */
    public static <T> T toBean(String json, Class<T> tClass) {
        try {
            return objectMapper.readValue(json, tClass);
        } catch (JsonProcessingException e) {
            log.error("json 反序列化错误：{}", json, e);
            return null;
        }
    }

    /**
     * 将 json 反序列化为集合
     *
     * @param json json 字符串
     * @return java.util.List<T> java 集合
     */
    public static <T> List<T> toList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("json 反序列化为集合错误：{}", json, e);
            return null;
        }
    }
}