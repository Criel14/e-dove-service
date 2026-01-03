package com.criel.edove.parcel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 出库接口的请求参数：需要区分是管理员出库还是用户出库
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckOutDTO implements Serializable {

    /**
     * 快递单号（前端直接识别条形码原本内容）
     */
    private String trackingNumber;

    /**
     * 出库用户展示的身份码（需要通过redis获取到用户手机号）
     * tip: 如果是管理员出库，则identityCode为空，同时需要验证当前用户是管理员
     */
    private String identityCode;

    /**
     * 机器ID：表示用户通过哪个设备出库
     * tip: 如果是管理员出库，则machineId为空
     */
    private Long machineId;
}
