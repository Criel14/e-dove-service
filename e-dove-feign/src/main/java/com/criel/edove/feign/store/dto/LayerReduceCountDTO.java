package com.criel.edove.feign.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 减少货架层的当前包裹数的请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LayerReduceCountDTO implements Serializable {

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 货架在门店中的编号
     */
    private Integer shelfNo;

    /**
     * 货架层的编号
     */
    private Integer layerNo;
}
