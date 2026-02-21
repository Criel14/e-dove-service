package com.criel.edove.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * AI聊天调用工具时的统一返回对象
 */
@Data
public class ToolResult<T> implements Serializable {

    /**
     * 成功或失败
     */
    private Boolean status;

    /**
     * 错误信息（成功时为空）
     */
    private String errorMessage;

    /**
     * 返回数据（失败时为空）
     */
    private T data;

    public static ToolResult<Void> success() {
        return success(null);
    }

    public static <T> ToolResult<T> success(T data) {
        ToolResult<T> result = new ToolResult<T>();
        result.data = data;
        result.status = true;
        return result;
    }

    public static <T> ToolResult<T> error(String message) {
        ToolResult<T> result = new ToolResult<>();
        result.errorMessage = message;
        result.status = false;
        return result;
    }
}
