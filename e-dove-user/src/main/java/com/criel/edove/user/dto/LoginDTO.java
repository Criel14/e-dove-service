package com.criel.edove.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录接口的请求数据
 */
@Data
@AllArgsConstructor
public class LoginDTO implements Serializable {

    // 手机号
    private String phone;

    // 邮箱
    private String email;

    // 密码
    private String password;

    // 验证码
    private String phoneOtp;

}
