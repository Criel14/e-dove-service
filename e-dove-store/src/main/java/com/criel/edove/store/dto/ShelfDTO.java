package com.criel.edove.store.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建/修改货架接口请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShelfDTO implements Serializable {

    /**
     * 货架id
     * tip：创建时为null，修改时不为null
     */
    private Long id;

    /**
     * 货架总层数
     */
    private Integer layerCount;

    /**
     * 门店内部货架编号（整数）
     * tip: 为null则默认为当前门店中最大货架编号+1，不为null则需要检查编号是否存在
     */
    private Integer shelfNo;

    /**
     * 货架可放包裹最大宽度（cm）
     */
    private BigDecimal maxWidth;

    /**
     * 货架可放包裹最大高度（cm）
     */
    private BigDecimal maxHeight;

    /**
     * 货架可放包裹最大长度（cm）
     */
    private BigDecimal maxLength;

    /**
     * 货架可承受最大重量（kg）
     */
    private BigDecimal maxWeight;

    /**
     * 货架状态（整型）：1=正常、0=停用或维修等
     */
    private Integer status;

}
