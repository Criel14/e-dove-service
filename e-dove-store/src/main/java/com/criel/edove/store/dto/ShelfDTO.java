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

    private Integer layerCount;

    // 门店内货架编号，为null则默认为当前门店中最大货架编号+1，不为null则需要检查编号是否存在
    private Integer shelfNo;

    private BigDecimal maxWidth;

    private BigDecimal maxHeight;

    private BigDecimal maxLength;

    private BigDecimal maxWeight;

}
