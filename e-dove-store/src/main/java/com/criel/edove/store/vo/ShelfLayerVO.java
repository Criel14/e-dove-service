package com.criel.edove.store.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 货架 + 货架层 查询接口响应数据：货架层子数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShelfLayerVO implements Serializable {

    /**
     * 货架层id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 层号编号（从 1 开始）
     */
    private Integer layerNo;

    /**
     * 当天最大序号，用于取件码序列（每日重置）
     */
    private Integer todayMaxSeq;

    /**
     * 最大编号上限 / 最多可存放包裹数量
     */
    private Integer maxCapacity;

}
