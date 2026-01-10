package com.criel.edove.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 注册接口的请求数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号（不能为空）
     */
    private String phone;

    /**
     * 邮箱（有则需要有邮箱验证码）
     */
    private String email;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 手机的验证码
     */
    private String phoneOtp;

    /**
     * 邮箱的验证码
     */
    private String emailOtp;

}
