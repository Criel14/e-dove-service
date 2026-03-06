package com.criel.edove.parcel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author Criel
 * @since 2026-03-06
 */
@Getter
@Setter
@Builder
@ToString
@TableName("outbox_event")
public class OutboxEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息唯一ID（雪花算法）
     */
    @TableId("event_id")
    private Long eventId;

    /**
     * 消息绑定名称
     */
    @TableField("binding_name")
    private String bindingName;

    /**
     * 消息内容（格式化的JSON对象）
     */
    @TableField("payload")
    private String payload;

    /**
     * 消息状态：0=未发送，1=已发送
     */
    @TableField("status")
    private Integer status;

    /**
     * 记录创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
