package com.criel.edove.common.constant;

/**
 * Redis key前缀
 */
public class RedisKeyConstant {

    // 用户的refresh token
    public static final String REFRESH_TOKEN_PREFIX = "refresh:token:";

    // refresh token黑名单
    public static final String REFRESH_TOKEN_BLACK_LIST = "refresh:token:black:list:";

    // 用户登录/注册验证码
    public static final String USER_OTP = "user:otp:";

    // 用户注册分布式锁
    public static final String USER_REGISTER_LOCK = "user:register:lock:";

    // 用户所属门店id
    public static final String USER_STORE_ID = "user:store:id:";

    // 新增货架分布式锁
    public static final String SHELF_CREATE_LOCK = "shelf:create:lock:";

}
