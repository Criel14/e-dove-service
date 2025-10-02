package com.criel.edove.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 修改用户认证信息接口响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserAuthDTO implements Serializable {

    private String username;

    private String email;

}
