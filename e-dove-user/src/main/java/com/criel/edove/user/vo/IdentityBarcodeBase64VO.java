package com.criel.edove.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 身份码生成响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentityBarcodeBase64VO implements Serializable {

    String barcodeBase64;

}
