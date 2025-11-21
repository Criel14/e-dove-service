package com.criel.edove.store.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 货架 + 货架层 查询接口响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShelfAndLayerVO implements Serializable {

    /**
     * 货架id
     */
    private Long id;

    /**
     * 所属门店 ID
     */
    private Long storeId;

    /**
     * 门店内部货架编号（整数）
     */
    private Integer shelfNo;

    /**
     * 货架总层数
     */
    private Integer layerCount;

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
     * tip：停用的货架前端应标出
     */
    private Integer status;

    /**
     * 货架层列表
     */
    private List<ShelfLayerVO> shelfLayers;

}
