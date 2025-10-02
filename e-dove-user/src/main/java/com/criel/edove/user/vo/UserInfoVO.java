package com.criel.edove.user.vo;

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

    // 所属门店ID（工作人员才有值）
    private Long storeId;

    private String username;

    private String phone;

    private String email;

    private String avatarUrl;

}
