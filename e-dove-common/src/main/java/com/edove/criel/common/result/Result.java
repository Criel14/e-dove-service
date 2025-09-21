package com.edove.criel.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回对象
 */
@Data
public class Result<T> implements Serializable {

    // 成功或失败
    private Boolean status;
    // 错误信息
    private String message;
    // 返回数据
    private T data;

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.status = true;
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.data = data;
        result.status = true;
        return result;
    }

    public static Result<Object> error(String message) {
        Result<Object> result = new Result<>();
        result.message = message;
        result.status = false;
        return result;
    }
}
