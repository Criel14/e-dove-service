package com.criel.edove.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改用户信息接口响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInfoDTO {

    private String username;

    private String email;

    // 修改邮箱需要邮箱验证码
    private String emailOtp;

    private String avatarUrl;

}
