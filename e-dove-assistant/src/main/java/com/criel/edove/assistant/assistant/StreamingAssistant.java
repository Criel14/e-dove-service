package com.criel.edove.assistant.assistant;

import dev.langchain4j.service.*;
import dev.langchain4j.service.spring.AiService;

/**
 * 流式输出大模型
 */
@AiService(
        streamingChatModel = "openAiStreamingChatModel",
        chatMemoryProvider = "chatMemoryProvider"
)
public interface StreamingAssistant {

    @SystemMessage(fromResource = "/prompt/system-prompt-admin.txt")
    TokenStream AdminChat(@MemoryId String memeryId, @UserMessage String message);

    @SystemMessage("你是一个快递驿站的智能助手")
    TokenStream UserChat(@MemoryId String memeryId, @UserMessage String message);

}
