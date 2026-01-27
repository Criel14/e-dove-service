package com.criel.edove.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 修改密码接口请求数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordDTO implements Serializable {

    /**
     * 手机验证码
     */
    private String phoneOtp;

    /**
     * 新密码
     */
    private String newPassword;

}
