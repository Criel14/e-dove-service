package com.criel.edove.assistant.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket 聊天响应体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WsChatResponseVO {

    /**
     * 消息类型：ack / token / done / error / pong
     */
    private String type;

    /**
     * 会话记忆ID
     */
    private String memoryId;

    /**
     * token 序号（仅 token 消息使用）
     */
    private Long seq;

    /**
     * token 内容（仅 token 消息使用）
     */
    private String content;

    /**
     * 错误信息（仅 error 消息使用）
     */
    private String error;

}
