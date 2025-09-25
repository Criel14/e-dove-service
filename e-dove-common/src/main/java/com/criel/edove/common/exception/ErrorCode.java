package com.criel.edove.common.exception;

import lombok.Getter;

/**
 * 自定义错误码和错误信息
 * 1xxx：通用错误
 * 2xxx：用户服务错误
 */
@Getter
public enum ErrorCode {

    SYSTEM_ERROR("1001", "系统异常，请联系管理员"),

    JWT_ERROR("2001", "用户信息校验异常"),
    REFRESH_TOKEN_ERROR("2002","用户信息校验异常"),
    USER_NOT_FOUND("2003","用户不存在"),
    LOGIN_PHONE_OTP_ERROR("2004","手机验证码错误"),
    PASSWORD_NOT_FOUND("2005","该用户未设置密码"),
    PASSWORD_ERROR("2006","密码错误"),
    LOGIN_MISSING_PARAMETER("2007","登录参数缺失"),
    REGISTER_MISSING_PARAMETER("2008","注册参数缺失"),
    REGISTER_PHONE_EXIST("2009","手机号已被注册"),
    REGISTER_EMAIL_EXIST("2010","邮箱已被注册"),
    REGISTER_PHONE_OTP_ERROR("2011","手机验证码错误"),
    REGISTER_EMAIL_OTP_ERROR("2012","邮箱验证码错误"),
    OTP_PARAMETER_ERROR("2013","手机号或邮箱格式错误"),;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
