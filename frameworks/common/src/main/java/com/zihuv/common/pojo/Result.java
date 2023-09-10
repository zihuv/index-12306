package com.zihuv.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    public static <T> Result<T> ok() {
        return Result.ok(null);
    }

    public static <T> Result<T> ok(T data) {
        return Result.ok("操作成功", data);
    }

    public static <T> Result<T> ok(String message, T data) {
        return Result.generate(200, message, data);
    }

    public static <T> Result<T> fail(String message) {
        return Result.fail(message, null);
    }

    public static <T> Result<T> fail(String message, T data) {
        return Result.generate(404, message, data);
    }

    /**
     * 封装Result对象，并返回该对象
     *
     * @param code    返回码
     * @param message 返回消息
     * @param data    返回数据
     * @return com.zihuv.common.model.vo.Result<T>
     */
    private static <T> Result<T> generate(Integer code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}