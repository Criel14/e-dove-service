package com.criel.edove.common.enumeration;

import lombok.Getter;

/**
 * 自定义错误码和错误信息
 * 1xxx：通用异常
 * 2xxx：auth服务异常
 * 3xxx：user服务异常
 * 4xxx: store服务异常
 * 5xxx: parcel服务异常
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
    REGISTER_LOCK_ERROR("2014","请求过于频繁，请稍后再试"),
    OTP_PARAMETER_ERROR("2015","手机号或邮箱格式错误"),
    OTP_MISSING_PARAMETER("2016","验证码请求参数缺失"),
    OTP_REQUEST_TOO_FREQUENTLY("2017","验证码请求频率过快"),

    // user服务异常
    USERINFO_MISSING_USER_ID("3001","用户信息缺失用户ID"),
    UPDATE_INFO_PHONE_ALREADY_EXISTS("3002","手机号已被注册"),
    UPDATE_INFO_EMAIL_ALREADY_EXISTS("3003","邮箱已被注册"),
    UPDATE_INFO_USERNAME_ALREADY_EXISTS("3004","用户名已被注册"),
    UPDATE_INFO_EMAIL_OTP_ERROR("3005","邮箱验证码错误"),
    UPDATE_INFO_EMAIL_PARAMETER_ERROR("3006","邮箱格式错误"),
    HMAC_ERROR("3007","HMAC-SHA256计算异常"),
    IDENTITY_CODE_VERIFY_EMPTY_ERROR("3008","身份码为空"),
    IDENTITY_CODE_VERIFY_EXPIRED_ERROR("3009","身份码已过期"),
    IDENTITY_CODE_VERIFY_ERROR("3010","身份码有误"),
    IDENTITY_CODE_LOCK_ERROR("3011", "操作过于频繁"),

    // store服务异常
    USER_STORE_NOT_BOUND_ERROR("4001", "用户未绑定门店"),
    STORE_NOT_FOUND_ERROR("4002", "门店不存在"),
    USER_STORE_BOUND_ERROR("4003", "用户绑定门店失败"),
    USER_STORE_BOUND_NOT_MATCHED("4004", "用户未绑定该门店"),
    SHELF_NO_ALREADY_EXISTS("4005", "货架编号重复"),
    SHELF_CREATE_LOCK_ERROR("4006", "操作过于频繁"),
    SHELF_LAYER_HAS_PARCELS("4007", "货架层上仍有包裹"),
    SHELF_NOT_BELONG_TO_STORE("4008", "货架不属于当前门店"),
    SHELF_LAYER_COUNT_LOCK_ERROR("4009", "操作过于频繁"),;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
