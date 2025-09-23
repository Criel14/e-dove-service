package com.criel.edove.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 封装刷新 refresh token 时的返回数据
 */
@Data
@AllArgsConstructor
public class TokenRefreshVO implements Serializable {

    private String accessToken;

    private String refreshToken;

}
