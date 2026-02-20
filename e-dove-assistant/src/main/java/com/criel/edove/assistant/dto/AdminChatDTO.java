package com.criel.edove.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 大模型聊天请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminChatDTO implements Serializable {

    /**
     * 会话ID
     */
    private String memoryId;

    /**
     * 聊天信息
     */
    private String message;

}
