package com.criel.edove.user.vo;

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

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 所属门店ID（工作人员才有值）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long storeId;

    private String username;

    private String phone;

    private String email;

    private String avatarUrl;

}
