package com.criel.edove.common.mq.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 事件(消息)父类
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEvent implements Serializable {

    /**
     * 事件ID（消息ID）
     */
    private Long eventId;

    /**
     * 事件发生时间（消息发送事件）
     */
    private LocalDateTime occurredAt;

}