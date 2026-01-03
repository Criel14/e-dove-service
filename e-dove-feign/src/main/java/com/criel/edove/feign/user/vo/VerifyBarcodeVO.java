package com.criel.edove.feign.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 身份码校验响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyBarcodeVO implements Serializable {

    private String phone;

}
