package com.criel.edove.common.constant;

/**
 * Redis key前缀
 */
public class RedisKeyConstant {

    // 用户的refresh token + 用户id
    public static final String REFRESH_TOKEN_PREFIX = "refresh:token:";

    // refresh token黑名单 + jti
    public static final String REFRESH_TOKEN_BLACK_LIST = "refresh:token:black:list:";

    // 用户登录/注册验证码 + 手机号或邮箱
    public static final String USER_OTP = "user:otp:";

    // 用户注册分布式锁 + 手机号
    public static final String USER_REGISTER_LOCK = "user:register:lock:";

    // 用户所属门店id + 用户id
    public static final String USER_STORE_ID = "user:store:id:";

    // 新增货架分布式锁 + 门店id
    public static final String SHELF_CREATE_LOCK = "shelf:create:lock:";

    // 货架数据修改操作分布式锁 + 货架id
    public static final String SHELF_UPDATE_LOCK = "shelf:update:lock:";

}
