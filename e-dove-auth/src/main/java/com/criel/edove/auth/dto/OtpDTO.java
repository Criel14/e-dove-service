package com.criel.edove.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 验证码接口请求数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpDTO implements Serializable {

    // 手机号或邮箱
    private String phoneOrEmail;

}
