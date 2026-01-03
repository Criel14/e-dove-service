package com.criel.edove.feign.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 包裹入库接口的包裹信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParcelCheckInDTO implements Serializable {

    /**
     * 包裹宽度（单位：cm）
     */
    private BigDecimal width;

    /**
     * 包裹高度（单位：cm）
     */
    private BigDecimal height;

    /**
     * 包裹长度（单位：cm）
     */
    private BigDecimal length;

    /**
     * 包裹重量（单位：kg）
     */
    private BigDecimal weight;

    /**
     * 送至门店 ID
     */
    private Long storeId;
}
