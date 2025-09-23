package com.criel.edove.common.context;

/**
 * 用 ThreadLocal 存储用户的上下文信息
 */
public class UserInfoContextHolder {

    private static final ThreadLocal<UserInfoContext> userInfoContext = new ThreadLocal<>();

    /**
     * 设置值
     */
    public static void setUserInfoContext(Long userId, String username, String phone) {
        UserInfoContext info = new UserInfoContext(userId, username, phone);
        userInfoContext.set(info);
    }

    /**
     * 获取值
     */
    public static UserInfoContext getUserInfoContext() {
        return userInfoContext.get();
    }

    /**
     * 清除值（防止内存泄漏）
     */
    public static void clearUserInfoContext() {
        userInfoContext.remove();
    }
}
