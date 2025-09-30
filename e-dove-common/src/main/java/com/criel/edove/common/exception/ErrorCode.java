package com.criel.edove.common.exception;

import lombok.Getter;

/**
 * 自定义错误码和错误信息
 * 1xxx：通用异常
 * 2xxx：auth服务异常
 * 3xxx：user服务异常
 */
@Getter
public enum ErrorCode {

    // 通用异常
    SYSTEM_ERROR("1001", "系统异常，请联系管理员"),

    // auth服务异常
    JWT_ERROR("2001", "用户信息校验异常"),
    REFRESH_TOKEN_ERROR("2002","用户信息校验异常"),
    USER_NOT_FOUND("2003","用户不存在"),
    PASSWORD_NOT_FOUND("2004","该用户未设置密码"),
    PASSWORD_ERROR("2005","密码错误"),
    SIGN_IN_MISSING_PARAMETER("2006","登录参数缺失"),
    SIGN_IN_PHONE_OTP_ERROR("2007","手机验证码错误"),
    REGISTER_MISSING_PARAMETER("2008","注册参数缺失"),
    REGISTER_PHONE_ALREADY_EXISTS("2009","手机号已被注册"),
    REGISTER_EMAIL_ALREADY_EXISTS("2010","邮箱已被注册"),
    REGISTER_USERNAME_ALREADY_EXISTS("2011","用户名已被注册"),
    REGISTER_PHONE_OTP_ERROR("2012","手机验证码错误"),
    REGISTER_EMAIL_OTP_ERROR("2013","邮箱验证码错误"),
    OTP_PARAMETER_ERROR("2014","手机号或邮箱格式错误"),
    OTP_MISSING_PARAMETER("2015","验证码请求参数缺失"),
    OTP_REQUEST_TOO_FREQUENTLY("2016","验证码请求频率过快"),

    // user服务异常
    USERINFO_MISSING_USER_ID("3001","用户信息缺失用户ID"),
    UPDATE_INFO_PHONE_ALREADY_EXISTS("3002","手机号已被注册"),
    UPDATE_INFO_EMAIL_ALREADY_EXISTS("3003","邮箱已被注册"),
    UPDATE_INFO_USERNAME_ALREADY_EXISTS("3004","用户名已被注册"),
    UPDATE_INFO_EMAIL_OTP_ERROR("3005","邮箱验证码错误"),
    UPDATE_INFO_EMAIL_PARAMETER_ERROR("3006","邮箱格式错误"),;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
