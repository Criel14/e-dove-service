package com.criel.edove.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 刷新 refresh token 接口的响应数据
 */
@Data
@AllArgsConstructor
public class TokenRefreshVO implements Serializable {

    private String accessToken;

    private String refreshToken;

}
