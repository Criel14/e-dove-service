package com.criel.edove.user.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 注册接口的请求数据
 */
@Data
@AllArgsConstructor
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
