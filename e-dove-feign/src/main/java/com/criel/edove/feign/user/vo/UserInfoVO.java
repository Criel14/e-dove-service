package com.criel.edove.feign.user.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户信息接口响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVO implements Serializable {

    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 所属门店ID（工作人员才有值）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long storeId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户手机号（必有）
     */
    private String phone;

    /**
     * 用户邮箱（绑定了才有值）
     */
    private String email;

    /**
     * 用户头像URL
     */
    private String avatarUrl;

}
