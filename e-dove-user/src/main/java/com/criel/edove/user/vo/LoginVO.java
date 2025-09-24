package com.criel.edove.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 登录接口的响应数据
 */
@Data
@AllArgsConstructor
public class LoginVO implements Serializable  {

    private String accessToken;

    private String refreshToken;

    // 用户ID（用String而不用Long，防止前端精度丢失）
    private String userId;

    // 用户名
    private String username;

    // 用户的角色名称
    private List<String> roleNames;

    // 用户的权限列表
    private List<String> permissionCodes;

}
