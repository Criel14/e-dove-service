package com.criel.edove.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 刷新 refresh token 接口的响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRefreshVO implements Serializable {

    private String accessToken;

    private String refreshToken;

}
