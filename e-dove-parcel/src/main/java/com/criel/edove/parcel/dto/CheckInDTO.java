package com.criel.edove.parcel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 入库接口的请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInDTO implements Serializable {

    /**
     * 快递单号（前端直接识别条形码原本内容）
     */
    private String trackingNumber;

}
