package com.criel.edove.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 身份码校验响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyBarcodeVO {

    private String phone;

}
