package com.criel.edove.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户信息接口响应数据
 */
@Data
@AllArgsConstructor
public class UserInfoVO {

    private Long userId;

    private String username;

    private String phone;

    private String email;

    private String avatarUrl;

    private String roleName;

    private String roleDesc;

}
