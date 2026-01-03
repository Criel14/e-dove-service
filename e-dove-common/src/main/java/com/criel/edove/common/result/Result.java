package com.criel.edove.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回对象
 */
@Data
public class Result<T> implements Serializable {

    /**
     * 成功或失败
     */
    private Boolean status;

    /**
     * 错误码（成功时为空）
     */
    private String code;

    /**
     * 错误信息（成功时为空）
     */
    private String message;

    /**
     * 返回数据（失败时为空）
     */
    private T data;

    public static Result<Void> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.data = data;
        result.status = true;
        return result;
    }

    public static Result<Object> error(String code, String message) {
        Result<Object> result = new Result<>();
        result.code = code;
        result.message = message;
        result.status = false;
        return result;
    }
}
