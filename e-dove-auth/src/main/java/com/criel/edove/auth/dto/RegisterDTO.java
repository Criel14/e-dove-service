package com.criel.edove.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册接口的请求数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {

    private String username;

    private String password;

    private String phone;

    private String email;

    // 头像地址
    private String avatarUrl;

    // 手机的验证码
    private String phoneOtp;

    // 邮箱的验证码
    private String emailOtp;

}
