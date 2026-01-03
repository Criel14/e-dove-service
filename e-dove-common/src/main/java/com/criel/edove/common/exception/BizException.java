package com.criel.edove.common.exception;

import com.criel.edove.common.enumeration.ErrorCode;
import lombok.Data;
import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BizException extends RuntimeException {

    // 业务错误码
    private final String code;

    // 补充信息（真正的错误信息已经在RuntimeException里面了）
    private final String extraMessage;

    /**
     * 构造方法：直接传入code和message
     */
    public BizException(String code, String message) {
        super(message);
        this.code = code;
        this.extraMessage = "";
    }

    /**
     * 构造方法：枚举，无补充信息
     * @param errorCode 错误码枚举
     */
    public BizException(ErrorCode errorCode) {
        this(errorCode, "");
    }

    /**
     * 构造方法：枚举 + 补充信息
     * @param errorCode 错误码枚举
     * @param extraMessage 需要返回的补充信息
     */
    public BizException(ErrorCode errorCode, String extraMessage) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.extraMessage = extraMessage;
    }
}
