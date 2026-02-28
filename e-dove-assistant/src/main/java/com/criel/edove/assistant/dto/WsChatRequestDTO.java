package com.criel.edove.assistant.dto;

import lombok.Data;

/**
 * WebSocket 聊天请求体
 */
@Data
public class WsChatRequestDTO {

    /**
     * 消息类型：chat / ping
     */
    private String type;

    /**
     * 会话记忆ID
     */
    private String memoryId;

    /**
     * 用户输入消息
     */
    private String message;

}
