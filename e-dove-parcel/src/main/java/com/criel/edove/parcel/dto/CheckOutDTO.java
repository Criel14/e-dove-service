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
     * tip: 【管理员出库】：前端直接传入
     */
    private String trackingNumber;

    /**
     * 出库用户展示的身份码（需要通过redis获取到用户手机号）
     * tip: 【管理员出库】：identityCode为空
     */
    private String identityCode;

    /**
     * 收件人手机号
     * tip：【管理员出库】：recipientPhone不为空，直接传入，不需要通过身份码传入
     */
    private String recipientPhone;

    /**
     * 机器ID：表示用户通过哪个设备出库
     * tip: 【管理员出库】：machineId为空
     */
    private Long machineId;
}
