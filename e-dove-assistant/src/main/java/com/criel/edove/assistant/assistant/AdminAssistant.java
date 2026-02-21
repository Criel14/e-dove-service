package com.criel.edove.assistant.assistant;

import dev.langchain4j.service.*;
import dev.langchain4j.service.spring.AiService;

/**
 * 流式输出大模型
 */
@AiService(
        streamingChatModel = "openAiStreamingChatModel",
        chatMemoryProvider = "chatMemoryProvider",
        tools = {
                "adminChatTool"
        }
)
public interface AdminAssistant {

    @SystemMessage(fromResource = "/prompt/system-prompt-admin.txt")
    TokenStream adminChat(@MemoryId String memeryId, @UserMessage String message);

}
