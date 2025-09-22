package com.criel.edove.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TokenRefreshVO implements Serializable {

    private String accessToken;

    private String refreshToken;

}
