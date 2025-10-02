package com.criel.edove.feign.user.vo;

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

    private Long userId;

    private String username;

    private String phone;

    private String email;

    private String avatarUrl;

}
