package com.criel.edove.feign.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 创建用户接口请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {

    private Long userId;

    private String username;

    private String phone;

    private String email;

    private String avatarUrl;

}
