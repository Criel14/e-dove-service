package com.criel.edove.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 刷新 token 请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshDTO implements Serializable {

    private String refreshToken;

}
