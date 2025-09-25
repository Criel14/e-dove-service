package com.criel.edove.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 验证码接口请求数据
 */
@Data
@AllArgsConstructor
public class OtpDTO {

    // 手机号或邮箱
    private String phoneOrEmail;

}
