package com.criel.edove.assistant.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 创建新会话接口响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatCreateVO implements Serializable {

    String memoryId;

}
