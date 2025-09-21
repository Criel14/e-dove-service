package com.edove.criel.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SYSTEM_ERROR("1001", "系统异常，请联系管理员");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
