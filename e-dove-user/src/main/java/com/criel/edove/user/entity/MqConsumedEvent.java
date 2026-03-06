package com.criel.edove.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 消息队列的消息去重表
 * </p>
 *
 * @author Criel
 * @since 2026-03-06
 */
@Getter
@Setter
@ToString
@TableName("mq_consumed_event")
public class MqConsumedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息唯一ID（雪花算法）
     */
    @TableId("event_id")
    private Long eventId;

    /**
     * 生产时间
     */
    @TableField("produce_time")
    private LocalDateTime produceTime;

    /**
     * 消费时间
     */
    @TableField(value = "consume_time", fill = FieldFill.INSERT)
    private LocalDateTime consumeTime;
}
