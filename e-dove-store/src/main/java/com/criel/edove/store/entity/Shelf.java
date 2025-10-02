package com.criel.edove.store.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 货架表
 * </p>
 *
 * @author Criel
 * @since 2025-10-02
 */
@Getter
@Setter
@ToString
@TableName("shelf")
public class Shelf implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 雪花算法生成的货架唯一 ID
     */
    @TableId("id")
    private Long id;

    /**
     * 所属门店 ID
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 门店内部货架编号（整数）
     */
    @TableField("shelf_no")
    private Integer shelfNo;

    /**
     * 货架总层数
     */
    @TableField("layer_count")
    private Integer layerCount;

    /**
     * 货架可放包裹最大宽度（cm）
     */
    @TableField("max_width")
    private BigDecimal maxWidth;

    /**
     * 货架可放包裹最大高度（cm）
     */
    @TableField("max_height")
    private BigDecimal maxHeight;

    /**
     * 货架可放包裹最大长度（cm）
     */
    @TableField("max_length")
    private BigDecimal maxLength;

    /**
     * 货架可承受最大重量（kg）
     */
    @TableField("max_weight")
    private BigDecimal maxWeight;

    /**
     * 货架状态（整型）：1=正常、0=停用或维修等
     */
    @TableField("status")
    private Integer status;

    /**
     * 记录创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 记录最后更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
