package com.criel.edove.common.exception;

import com.criel.edove.common.enumeration.ErrorCode;
import lombok.Getter;

/**
 * 业务异常类基类
 */
@Getter
public abstract class BaseException extends RuntimeException {

    // 业务错误码
    private final String code;

    // 补充信息
    private final String extraMessage;

    /**
     * 构造方法
     * @param errorCode 错误码枚举
     */
    public BaseException(ErrorCode errorCode) {
        this(errorCode, "");
    }

    /**
     * 构造方法
     * @param errorCode 错误码枚举
     * @param extraMessage 需要返回的补充信息
     */
    public BaseException(ErrorCode errorCode, String extraMessage) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.extraMessage = extraMessage;
    }
}
