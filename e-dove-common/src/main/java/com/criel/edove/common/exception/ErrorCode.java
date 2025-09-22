package com.criel.edove.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SYSTEM_ERROR("1001", "系统异常，请联系管理员"),
    JWT_ERROR("1002", "用户信息校验异常"),
    REFRESH_TOKEN_ERROR("1003","token刷新异常");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
