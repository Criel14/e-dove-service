package com.criel.edove.store.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 货架层表
 * </p>
 *
 * @author Criel
 * @since 2025-10-02
 */
@Getter
@Setter
@ToString
@TableName("shelf_layer")
public class ShelfLayer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 雪花算法生成的层唯一 ID
     */
    @TableId("id")
    private Long id;

    /**
     * 所属货架 ID
     */
    @TableField("shelf_id")
    private Long shelfId;

    /**
     * 层号编号（从 1 开始）
     */
    @TableField("layer_no")
    private Integer layerNo;

    /**
     * 当天最大序号，用于取件码序列（每日重置）
     */
    @TableField("today_max_seq")
    private Integer todayMaxSeq;

    /**
     * 最大编号上限 / 最多可存放包裹数量
     */
    @TableField("max_capacity")
    private Integer maxCapacity;

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
