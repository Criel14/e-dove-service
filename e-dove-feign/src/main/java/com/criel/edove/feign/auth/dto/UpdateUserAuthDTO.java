package com.criel.edove.feign.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改用户认证信息接口响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserAuthDTO {

    private String username;

    private String email;

}
