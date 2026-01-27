package com.criel.edove.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录接口的请求数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInDTO implements Serializable {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机验证码
     */
    private String phoneOtp;

}
