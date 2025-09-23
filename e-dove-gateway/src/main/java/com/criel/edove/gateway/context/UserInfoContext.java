package com.criel.edove.gateway.context;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户的上下文信息：在 UserInfoContextHolder 类中使用
 */
@Data
@AllArgsConstructor
public class UserInfoContext {

    // 用户ID
    private Long userId;

    // 用户名
    private String username;

    // 手机号
    private String phone;
}
