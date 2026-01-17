package com.criel.edove.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 修改用户信息接口请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInfoDTO implements Serializable {

    private String username;

    private String email;

    /**
     * 修改邮箱需要邮箱验证码
     */
    private String emailOtp;

    /**
     * 头像地址
     */
    private String avatarUrl;

}
